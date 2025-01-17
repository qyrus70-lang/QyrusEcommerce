import random

# Placeholder for image
placeholder_image = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/1280px-Placeholder_view_vector.svg.png"

# Product categories
product_categories = {
    "Men": ["T-Shirts", "Jeans", "Shirts"],
    "Women": ["Dresses", "Tops", "Skirts"],
    "Kids": ["Toys", "Clothing", "Books"],
    "Accessories": ["Watches", "Bags", "Jewelry"]
}

# Attributes for random generation
colors = [
    {"name": "Red", "hex": "#FF0000"},
    {"name": "Blue", "hex": "#0000FF"},
    {"name": "Green", "hex": "#008000"},
    {"name": "Yellow", "hex": "#FFFF00"},
    {"name": "Black", "hex": "#000000"},
    {"name": "Pink", "hex": "#FFC0CB"}
]
sizes = ["S", "M", "L", "XL"]
providers = ["Provider A", "Provider B", "Provider C", "Provider D"]

# Generate products_db with 20 rows for each subcategory
products_db = []
product_id = 1

for category, subcategories in product_categories.items():
    for subcategory in subcategories:
        for _ in range(20):  # Generate 20 products per subcategory
            product = {
                "id": product_id,
                "name": f"{category[:-1]}'s {subcategory[:-1]} {product_id}",
                "price": random.randint(50, 500),
                "image": placeholder_image,
                "category": category,
                "subcategory": subcategory,
                "sizes": random.sample(sizes, random.randint(1, len(sizes))),
                "colors": random.sample(colors, random.randint(1, len(colors))),
                "providers": random.sample(providers, random.randint(1, len(providers)))
            }
            products_db.append(product)
            product_id += 1

# Adding gibberish descriptions, ratings, and comments to the products
gibberish_descriptions = [
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere.",
    "Curabitur lacinia felis vel justo fringilla, non elementum est congue.",
    "Pellentesque habitant morbi tristique senectus et netus et malesuada fames.",
    "Donec ultricies sem a arcu vehicula, id tincidunt massa posuere.",
    "Fusce varius ligula sed ex convallis, nec accumsan risus blandit.",
    "Aliquam erat volutpat. Cras interdum nisl non erat tempor gravida.",
    "Etiam convallis metus eget augue sodales, vel varius justo aliquam.",
    "Nulla facilisi. Integer pulvinar lorem id sapien faucibus tincidunt.",
    "Suspendisse quis lacus sed orci eleifend volutpat ac nec lectus."
]

comments = {
    "positive": [
        "Great product!", "Highly recommend.", "Would buy again.",
        "Good value for money.", "Excellent quality.", "Stylish and comfortable."
    ],
    "negative": [
        "Not satisfied.", "Could be better.", "Not worth the price.", "Product is okay.", 
        "Don't buy this", "Waste of money", "You will regret", "Never Buy this"
    ]
}

ratings = [1, 2, 3, 4, 5]  # Ratings from 1 to 5

# Add new attributes to the products
for product in products_db:
    product["description"] = random.choice(gibberish_descriptions)
    rating = random.choice(ratings)
    product["rating"] = rating
    
    if rating >= 4:  # Positive rating
        num_comments = random.randint(1, 5)
        product["comments"] = random.sample(comments["positive"], num_comments)
    elif rating <= 2:  # Negative rating
        num_comments = random.randint(1, 5)
        product["comments"] = random.sample(comments["negative"], num_comments)
    else:  # Neutral rating
        num_comments_positive = random.randint(0, 2)  # Mix of positive and negative
        num_comments_negative = random.randint(0, 2)
        product["comments"] = (
            random.sample(comments["positive"], num_comments_positive)
            + random.sample(comments["negative"], num_comments_negative)
        )

    # Optional: Shuffle the comments list to ensure randomness
    random.shuffle(product["comments"])
