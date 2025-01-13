from fastapi import FastAPI, HTTPException, Depends, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
from typing import Optional
import uuid
import uvicorn

app = FastAPI()

# Add CORS middleware
origins = [
    "http://localhost",
    "http://localhost:3000",
    "*"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mock database for demonstration purposes
users_db = {"admin@qyrus.com": {"password": "Qyrus@321"}}
verification_tokens = {}
password_reset_tokens = {}

placeholder_image = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/1280px-Placeholder_view_vector.svg.png"

# Product categories and subcategories
product_categories = {
    "Men": ["T-Shirts", "Jeans", "Shirts"],
    "Women": ["Dresses", "Tops", "Skirts"],
    "Kids": ["Toys", "Clothing", "Books"],
    "Accessories": ["Watches", "Bags", "Jewelry"]
}

products_db = [
    {"id": 1, "name": "Men's T-Shirt", "price": 100, "image": "image1.jpg", "category": "Men", "subcategory": "T-Shirts", "sizes": ["S", "M", "L"], "colors": [{"name": "Red", "hex": "#FF0000"}, {"name": "Blue", "hex": "#0000FF"}], "providers": ["Provider A"]},
    {"id": 2, "name": "Women's Dress", "price": 200, "image": "image2.jpg", "category": "Women", "subcategory": "Dresses", "sizes": ["M", "L"], "colors": [{"name": "Black", "hex": "#000000"}, {"name": "Green", "hex": "#008000"}], "providers": ["Provider B"]},
    {"id": 3, "name": "Kid's Toy", "price": 150, "image": "image3.jpg", "category": "Kids", "subcategory": "Toys", "sizes": ["M", "L"], "colors": [{"name": "Yellow", "hex": "#FFFF00"}], "providers": ["Provider C"]},
    {"id": 4, "name": "Men's Jeans", "price": 300, "image": "image4.jpg", "category": "Men", "subcategory": "Jeans", "sizes": ["M", "L", "XL"], "colors": [{"name": "Blue", "hex": "#0000FF"}], "providers": ["Provider A"]},
    {"id": 5, "name": "Women's Top", "price": 120, "image": "image5.jpg", "category": "Women", "subcategory": "Tops", "sizes": ["S", "M"], "colors": [{"name": "Pink", "hex": "#FFC0CB"}], "providers": ["Provider B"]},
    {"id": 6, "name": "Kid's Book", "price": 80, "image": "image6.jpg", "category": "Kids", "subcategory": "Books", "sizes": ["M", "L"], "colors": [{"name": "Black", "hex": "#000000"}, {"name": "Green", "hex": "#008000"}], "providers": ["Provider C"]}
]


class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class SignupRequest(BaseModel):
    email: EmailStr
    password: str

class VerifyEmailRequest(BaseModel):
    otp: str
    token: str

class ForgotPasswordRequest(BaseModel):
    email: EmailStr

class ResetPasswordRequest(BaseModel):
    password: str
    otp: str
    token: str

@app.post("/auth/login")
def login(request: LoginRequest):
    user = users_db.get(request.email)
    if user and user['password'] == request.password:
        return {"message": "Login successful"}
    raise HTTPException(status_code=401, detail="Invalid email or password")

@app.post("/auth/signup")
def signup(request: SignupRequest):
    if request.email in users_db:
        raise HTTPException(status_code=400, detail="Email already registered")
    users_db[request.email] = {
        "password": request.password,
        "verified": False
    }
    # Generate verification token (mocked)
    token = str(uuid.uuid4())
    verification_tokens[token] = {
        "email": request.email,
        "otp": "123456"
    }
    return {"message": "Signup successful. Please verify your email.", "token": token}

@app.post("/auth/verify-email")
def verify_email(request: VerifyEmailRequest):
    token_data = verification_tokens.get(request.token)
    if token_data and token_data["otp"] == request.otp:
        user_email = token_data["email"]
        users_db[user_email]["verified"] = True
        del verification_tokens[request.token]
        return {"message": "Email verified successfully"}
    raise HTTPException(status_code=400, detail="Invalid OTP or token")

@app.post("/auth/forgot-password")
def forgot_password(request: ForgotPasswordRequest):
    if request.email in users_db:
        # Generate password reset token (mocked)
        token = str(uuid.uuid4())
        password_reset_tokens[token] = {
            "email": request.email,
            "otp": "reset123"
        }
        return {"message": "Password reset link sent to your email", "token": token}
    raise HTTPException(status_code=404, detail="Email not registered")

@app.post("/auth/reset-password")
def reset_password(request: ResetPasswordRequest):
    token_data = password_reset_tokens.get(request.token)
    if token_data and token_data["otp"] == request.otp:
        user_email = token_data["email"]
        users_db[user_email]["password"] = request.password
        del password_reset_tokens[request.token]
        return {"message": "Password reset successfully"}
    raise HTTPException(status_code=400, detail="Invalid OTP or token")

@app.get("/get-products/")
def get_products(category: str = Query(...), page: int = Query(...)):
    filtered_products = [p for p in products_db if p["category"] == category]
    page_size = 2
    start = (page - 1) * page_size
    end = start + page_size
    total_pages = (len(filtered_products) + page_size - 1) // page_size
    return {
        "products": filtered_products[start:end],
        "total_pages": total_pages
    }

@app.get("/search-products/")
def search_products(query: str = Query(...)):
    filtered_products = [p for p in products_db if query.lower() in p["name"].lower()]
    return {
        "products": filtered_products
    }

@app.get("/get-product-categories/")
def get_product_categories():
    return {
        "categories": product_categories
    }

@app.get("/get-product-details/")
def get_product_details(product_id: int = Query(...)):
    product = next((p for p in products_db if p["id"] == product_id), None)
    if product:
        return {
            "id": product["id"],
            "name": product["name"],
            "price": product["price"],
            "image": product["image"],
            "category": product["category"],
            "subcategory": product["subcategory"],
            "sizes": product.get("sizes"),
            "colors": product.get("colors"),
            "providers": product.get("providers")
        }
    raise HTTPException(status_code=404, detail="Product not found")
if __name__ == "__main__":
    uvicorn.run('app:app', port=8888, reload=True)    