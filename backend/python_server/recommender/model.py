from typing import Optional, List
from pydantic import BaseModel


class PostDataForScoring(BaseModel):
    post_id: str
    title: str
    description: str
    seller_reputation: float
    post_images_url: Optional[List[Optional[str]]]

class BuyerProfile(BaseModel):
    user_id: str
    avg_price_liked: float
    preferred_categories: List[str]
    addresses: Optional[List[dict]]  # Optional if unused
    liked_post_title: List[str]
    liked_post_description: List[str]

class ScoringRequest(BaseModel):
    post: PostDataForScoring
    buyer: BuyerProfile

class ScoringResponse(BaseModel):
    post_id: str
    title_keyword_overlap: float
    description_keyword_overlap: float
    image_text_score: float