import re
import string
from io import BytesIO
from typing import Optional, List

import numpy as np
import requests
from PIL import Image, ImageFilter
from underthesea import word_tokenize

with open(r".\vietnamese-stopwords.txt", encoding="utf-8") as f:
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

def compression_score(image_data: bytes) -> float:
    size_kb = len(image_data) / 1024
    if size_kb >= 300:
        return 1.0
    elif size_kb >= 150:
        return 0.6
    elif size_kb >= 70:
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

def sharpness_score(image: Image.Image) -> float:
    gray = image.convert("L")
    laplacian = gray.filter(ImageFilter.FIND_EDGES)
    variance = np.var(np.array(laplacian))

    if variance > 1500:
        return 1.0
    elif variance > 500:
        return 0.6
    elif variance > 100:
        return 0.3
    else:
        return 0.1

def lighting_uniformity_score(image: Image.Image) -> float:
    gray = np.array(image.convert("L"))
    left = gray[:, :gray.shape[1]//2]
    right = gray[:, gray.shape[1]//2:]
    diff = abs(np.mean(left) - np.mean(right))
    if diff < 10:
        return 1.0
    elif diff < 30:
        return 0.6
    elif diff < 50:
        return 0.3
    else:
        return 0.1

def snr_score(image: Image.Image) -> float:
    gray = np.array(image.convert("L")).astype(np.float32)
    mean = np.mean(gray)
    std = np.std(gray)
    snr = mean / (std + 1e-5)
    if snr > 20:
        return 1.0
    elif snr > 10:
        return 0.6
    elif snr > 5:
        return 0.3
    else:
        return 0.1

def score_image(image_url: str, reference_text_user: str, reference_text_self: str):
    if not image_url:
        return 0.0
    try:
        response = requests.get(image_url, timeout=3)
        image = Image.open(BytesIO(response.content)).convert("RGB")

        scores = {
            "resolution": resolution_score(image),
            "brightness": brightness_score(image),
            "sharpness": sharpness_score(image),
            "compression": compression_score(response.content),
            "lighting_uniformity": lighting_uniformity_score(image),
            "snr": snr_score(image),
        }

        weights = {
            "resolution": 10,
            "brightness": 10,
            "sharpness": 10,
            "compression": 5,
            "lighting_uniformity": 10,
            "snr": 10
        }

        total_score = sum(scores[k] * w for k, w in weights.items())
        total_weight = sum(weights.values())
        normalized_score = total_score / total_weight if total_weight > 0 else 0.0

        return normalized_score

    except Exception as e:
        print(f"[Lỗi phân tích ảnh]: {e}")
        return 0.0