package com.example.forklore.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forklore.data.model.Post

class SearchViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Post>>()
    val searchResults: LiveData<List<Post>> = _searchResults

    private val allRecipes = createMockData()

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        _searchResults.value = allRecipes.filter {
            it.title.contains(query, ignoreCase = true) || it.story.contains(query, ignoreCase = true)
        }
    }

    private fun createMockData(): List<Post> {
        return listOf(
            Post(
                id = "1",
                ownerName = "Chef John",
                title = "Spaghetti Carbonara",
                story = "A classic Italian pasta dish.",
                ingredients = "Spaghetti, eggs, pancetta, pecorino cheese, black pepper",
                steps = "1. Cook spaghetti. 2. Fry pancetta. 3. Mix eggs and cheese. 4. Combine everything."
            ),
            Post(
                id = "2",
                ownerName = "Jane Doe",
                title = "Chicken Tikka Masala",
                story = "A popular Indian curry.",
                ingredients = "Chicken, yogurt, tomato sauce, spices",
                steps = "1. Marinate chicken. 2. Grill chicken. 3. Prepare curry sauce. 4. Combine and simmer."
            ),
            Post(
                id = "3",
                ownerName = "Foodie123",
                title = "Chocolate Chip Cookies",
                story = "The best chocolate chip cookies ever!",
                ingredients = "Flour, butter, sugar, eggs, chocolate chips",
                steps = "1. Cream butter and sugar. 2. Beat in eggs. 3. Stir in dry ingredients. 4. Add chocolate chips. 5. Bake."
            )
        )
    }
}
