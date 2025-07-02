import uvicorn
from fastapi import FastAPI

from recommender.model import ScoringResponse, ScoringRequest
from recommender.score_function import keyword_overlap, score_image

app = FastAPI()

@app.post("/score", response_model=ScoringResponse)
def score_post(data: ScoringRequest):
    post = data.post
    buyer = data.buyer

    title_overlap = keyword_overlap(post.title, buyer.liked_post_title)
    desc_overlap = keyword_overlap(post.description, buyer.liked_post_description)

    # Only score thumbnail
    image_url = post.post_images_urls[0] if post.post_images_urls else None
    image_score = score_image(image_url)

    return ScoringResponse(
        post_id=post.post_id,
        title_keyword_overlap=round(title_overlap, 4),
        description_keyword_overlap=round(desc_overlap, 4),
        image_text_score=round(image_score, 4)
    )

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=5000, reload=True)
