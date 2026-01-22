package com.example.forklore.data

import com.example.forklore.data.model.Post

val mockPosts = listOf(
    Post(
        id = "1",
        ownerName = "John Doe",
        title = "Classic Spaghetti Bolognese",
        story = "A family recipe passed down through generations.",
        ingredients = "Spaghetti, minced beef, tomatoes, onion, garlic, herbs",
        steps = "1. Cook spaghetti. 2. Brown mince. 3. Add sauce ingredients. 4. Simmer. 5. Serve.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/appforklore.appspot.com/o/images%2Fspaghetti.jpg?alt=media&token=e937f0b7-2512-45a9-99d9-2475a3632940",
        tags = listOf("Italian", "Pasta", "Classic"),
        likesCount = 150,
        commentsCount = 25,
        createdAt = System.currentTimeMillis() - 86400000L * 1
    ),
    Post(
        id = "2",
        ownerName = "Jane Smith",
        title = "Avocado Toast with a Twist",
        story = "My go-to healthy and delicious breakfast.",
        ingredients = "Sourdough bread, avocado, feta cheese, red pepper flakes, lime",
        steps = "1. Toast bread. 2. Mash avocado with lime juice. 3. Spread on toast. 4. Top with feta and pepper flakes.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/appforklore.appspot.com/o/images%2Favocado_toast.jpg?alt=media&token=e6b2e7a1-8e5c-4a3e-b873-1f1e1f9a5621",
        tags = listOf("Breakfast", "Healthy", "Quick"),
        likesCount = 250,
        commentsCount = 45,
        createdAt = System.currentTimeMillis() - 86400000L * 2
    ),
    Post(
        id = "3",
        ownerName = "Chef Anton",
        title = "Spicy Chicken Tacos",
        story = "A taste of Mexico in your own kitchen! Quick, easy, and full of flavor.",
        ingredients = "Chicken breast, tortillas, salsa, jalapeños, cheese, lettuce",
        steps = "1. Grill chicken. 2. Chop vegetables. 3. Warm tortillas. 4. Assemble tacos.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/appforklore.appspot.com/o/images%2Ftacos.jpg?alt=media&token=c2b1a8f3-1383-4e4b-8531-3e421c613143",
        tags = listOf("Mexican", "Spicy", "Dinner"),
        likesCount = 320,
        commentsCount = 60,
        createdAt = System.currentTimeMillis() - 86400000L * 3
    ),
    Post(
        id = "4",
        ownerName = "Maria Garcia",
        title = "Creamy Tomato Soup",
        story = "The perfect comfort food for a rainy day. Best served with grilled cheese.",
        ingredients = "Tomatoes, cream, vegetable broth, basil, garlic",
        steps = "1. Sauté garlic. 2. Add tomatoes and broth. 3. Simmer. 4. Blend until smooth. 5. Stir in cream and basil.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/appforklore.appspot.com/o/images%2Ftomato_soup.jpg?alt=media&token=d8b3c8f1-80a8-4c1d-9e6e-2f2b3e8a4a58",
        tags = listOf("Soup", "Vegetarian", "Comfort Food"),
        likesCount = 180,
        commentsCount = 30,
        createdAt = System.currentTimeMillis() - 86400000L * 4
    ),
    Post(
        id = "5",
        ownerName = "Chris Lee",
        title = "Blueberry Pancakes",
        story = "Fluffy pancakes bursting with fresh blueberries. A weekend favorite!",
        ingredients = "Flour, milk, eggs, sugar, blueberries, baking powder",
        steps = "1. Mix dry ingredients. 2. Mix wet ingredients. 3. Combine and fold in blueberries. 4. Cook on a griddle.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/appforklore.appspot.com/o/images%2Fpancakes.jpg?alt=media&token=4c7d2b8e-3e4a-4a6a-8b8a-3e4c4d5b2a3a",
        tags = listOf("Breakfast", "Sweet", "Family"),
        likesCount = 400,
        commentsCount = 75,
        createdAt = System.currentTimeMillis() - 86400000L * 5
    ),
    Post(
        id = "6",
        ownerName = "Laura Chen",
        title = "Healthy Buddha Bowl",
        story = "A vibrant and nutritious bowl packed with veggies, grains, and a tasty dressing.",
        ingredients = "Quinoa, chickpeas, sweet potato, spinach, avocado, tahini dressing",
        steps = "1. Cook quinoa. 2. Roast sweet potato and chickpeas. 3. Assemble all ingredients in a bowl. 4. Drizzle with dressing.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/appforklore.appspot.com/o/images%2Fbuddha_bowl.jpg?alt=media&token=8a1b4c3e-9b3a-4e2a-b3e4-1b2c3d4e5f6a",
        tags = listOf("Healthy", "Vegan", "Lunch"),
        likesCount = 280,
        commentsCount = 50,
        createdAt = System.currentTimeMillis() - 86400000L * 6
    )
)
