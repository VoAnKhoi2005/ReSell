import re
import string
from io import BytesIO
from typing import Optional, List

import numpy as np
import open_clip
import requests
import torch
from PIL import Image
from underthesea import word_tokenize

# Khởi tạo device
device = "cuda" if torch.cuda.is_available() else "cpu"

# Tải mô hình CLIP và tokenizer
model, _, preprocess = open_clip.create_model_and_transforms("ViT-B-32", pretrained="openai")
tokenizer = open_clip.get_tokenizer("ViT-B-32")
model.to(device).eval()

with open("vietnamese-stopwords.txt", encoding="utf-8") as f:
    vietnamese_stopwords = set(line.strip() for line in f if line.strip())

def preprocess_text(text: str) -> List[str]:
    text = text.lower()
    text = re.sub(f"[{re.escape(string.punctuation)}]", "", text)
    tokens = word_tokenize(text, format="text").split()
    return [token for token in tokens if token not in vietnamese_stopwords and token.isalpha()]

def keyword_overlap(post_text: str, liked_texts: List[str]) -> float:
    post_tokens = set(preprocess_text(post_text))
    liked_tokens = set()
    for text in liked_texts:
        liked_tokens.update(preprocess_text(text))
    return len(post_tokens & liked_tokens) / len(post_tokens) if post_tokens else 0.0

def clip_similarity(image: Image.Image, reference_text_user: str, reference_text_self: str):
    image_input = preprocess(image).unsqueeze(0).to(device)
    text_user_input = tokenizer([reference_text_user]).to(device)
    text_self_input = tokenizer([reference_text_self]).to(device)

    with torch.no_grad():
        image_features = model.encode_image(image_input)
        user_text_features = model.encode_text(text_user_input)
        self_text_features = model.encode_text(text_self_input)

        image_features /= image_features.norm(dim=-1, keepdim=True)
        user_text_features /= user_text_features.norm(dim=-1, keepdim=True)
        self_text_features /= self_text_features.norm(dim=-1, keepdim=True)

        score_user = (image_features @ user_text_features.T).item()
        score_self = (image_features @ self_text_features.T).item()

        return score_user, score_self

def resolution_score(image: Image.Image) -> float:
    width, height = image.size
    pixels = width * height
    if pixels >= 1024 * 1024:
        return 1.0
    elif pixels >= 512 * 512:
        return 0.6
    elif pixels >= 300 * 300:
        return 0.3
    else:
        return 0.1

def brightness_score(image: Image.Image) -> float:
    gray = image.convert("L")
    brightness = np.array(gray).mean()
    if brightness < 40 or brightness > 220:
        return 0.2
    elif 100 <= brightness <= 180:
        return 1.0
    else:
        return 0.6

def score_image(image_url: str, reference_text_user: str, reference_text_self: str):
    if not image_url:
        return 0.0
    try:
        response = requests.get(image_url, timeout=3)
        image = Image.open(BytesIO(response.content)).convert("RGB")

        score_user, score_self = clip_similarity(image, reference_text_user, reference_text_self)
        res_score = resolution_score(image)
        bright_score = brightness_score(image)

        total_score = (
            0.4 * score_user +
            0.3 * score_self +
            0.15 * res_score +
            0.15 * bright_score
        )
        return total_score

    except Exception as e:
        print(f"[Lỗi phân tích ảnh]: {e}")
        return 0.0