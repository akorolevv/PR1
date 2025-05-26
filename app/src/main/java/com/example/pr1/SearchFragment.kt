package com.example.pr1

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pr1.data.ExerciseRepository
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.FragmentSearchBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchFragment : Fragment() {

    companion object {
        private const val TAG = "SearchFragment"
        private const val SEARCH_HISTORY_PREFS = "search_history_preferences"
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_ITEMS = 10
        private const val SEARCH_DEBOUNCE_DELAY = 2000L // 2 секунды для автоматического поиска
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter

    private var currentQuery: String = ""
    private var isSearching = false
    private var lastRealQuery: String = ""
    private var searchHistory = mutableListOf<String>()

    // Handler для реализации поиска с задержкой (debounce)
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch(currentQuery) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseRepository = ExerciseRepository(requireContext())

        setupRecyclerViews()
        setupSearchInput()
        setupEmptyState()
        loadSearchHistory()
        setupClearHistoryButton()

        // Восстанавливаем текст поискового запроса, если он был сохранен
        savedInstanceState?.getString("SEARCH_QUERY")?.let {
            currentQuery = it
            binding.searchInput.setText(it)
            binding.searchInput.setSelection(it.length)
            if (it.isNotEmpty()) {
                performSearch(it)
            }
        }
    }

    private fun setupRecyclerViews() {
        // Настройка адаптера для результатов поиска
        exerciseAdapter = ExerciseAdapter(emptyList()) { exercise ->
            // Добавляем элемент в историю при клике на результат
            addToSearchHistory(exercise.name)
        }

        binding.searchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exerciseAdapter
        }

        // Настройка адаптера для истории поиска
        historyAdapter = SearchHistoryAdapter(emptyList()) { query ->
            // При клике на элемент истории выполняем поиск
            binding.searchInput.setText(query)
            binding.searchInput.setSelection(query.length)
            currentQuery = query
            performSearch(query)
            hideSearchHistory()
        }

        binding.searchHistoryRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupSearchInput() {
        // Показываем подсказку, если поле пустое
        binding.searchInput.hint = getString(R.string.search_hint)

        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInput.text.isEmpty() && searchHistory.isNotEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                currentQuery = query

                // Отмена предыдущей задачи поиска всегда
                handler.removeCallbacks(searchRunnable)

                if (query.isNotEmpty()) {
                    binding.clearSearch.visibility = View.VISIBLE

                    // Планирование новой задачи поиска с задержкой
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)

                    // Скрываем историю поиска при вводе текста
                    hideSearchHistory()
                } else {
                    binding.clearSearch.visibility = View.GONE

                    // Если поле ввода пустое и есть история поиска, показываем её
                    if (searchHistory.isNotEmpty()) {
                        showSearchHistory()
                    } else {
                        showEmptyState()
                    }

                    // При пустом запросе нужно отменить любые активные поиски
                    isSearching = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Настраиваем кнопку очистки
        binding.clearSearch.setOnClickListener {
            binding.searchInput.setText("")
            currentQuery = ""

            // Очень важно: при очистке поля отменяем любые отложенные поиски
            handler.removeCallbacks(searchRunnable)

            // Скрываем клавиатуру
            hideKeyboard()

            // Если есть история поиска, показываем её, иначе пустое состояние
            if (searchHistory.isNotEmpty()) {
                showSearchHistory()
            } else {
                showEmptyState()
            }

            // Прерываем любые активные поиски
            isSearching = false
        }
    }

    private fun setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener {
            // Очищаем историю поиска
            searchHistory.clear()
            historyAdapter.updateItems(searchHistory)
            saveSearchHistory()

            // Скрываем контейнер с историей
            hideSearchHistory()

            // Показываем пустое состояние
            showEmptyState()
        }
    }

    private fun showSearchHistory() {
        binding.searchHistoryContainer.visibility = View.VISIBLE
        binding.emptySearchContainer.visibility = View.GONE
        binding.searchResults.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE

        // Обновляем адаптер истории поиска
        historyAdapter.updateItems(searchHistory)
    }

    private fun hideSearchHistory() {
        binding.searchHistoryContainer.visibility = View.GONE
    }

    private fun loadSearchHistory() {
        val sharedPrefs = requireContext().getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
        val historyJson = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)

        if (!historyJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<String>>() {}.type
            try {
                val loadedHistory = Gson().fromJson<List<String>>(historyJson, type)
                searchHistory.clear()
                searchHistory.addAll(loadedHistory)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading search history: ${e.message}")
            }
        }
    }

    private fun saveSearchHistory() {
        val sharedPrefs = requireContext().getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)
        val historyJson = Gson().toJson(searchHistory)

        sharedPrefs.edit {
            putString(SEARCH_HISTORY_KEY, historyJson)
            apply()
        }
    }

    private fun addToSearchHistory(query: String) {
        // Если элемент уже есть в истории, удаляем его
        searchHistory.remove(query)

        // Добавляем элемент в начало списка
        searchHistory.add(0, query)

        // Ограничиваем количество элементов в истории
        if (searchHistory.size > MAX_HISTORY_ITEMS) {
            searchHistory.removeAt(searchHistory.size - 1)
        }

        // Сохраняем обновленную историю
        saveSearchHistory()
    }

    private fun setupEmptyState() {
        // Находим кнопку "Обновить" и настраиваем её нажатие
        val retryButton = binding.emptySearchContainer.findViewById<Button>(R.id.retry_button)
        retryButton?.setOnClickListener {
            Log.d(TAG, "Retry button clicked, currentQuery: $currentQuery")

            // Проверяем, что текущий запрос не пустой
            if (currentQuery.isNotEmpty() && !isSearching) {
                // Если текущий запрос содержит "error", заменяем его на последний реальный запрос
                if (currentQuery.contains("error", ignoreCase = true)) {
                    // Если был какой-то реальный запрос, используем его
                    if (lastRealQuery.isNotEmpty()) {
                        // Устанавливаем последний успешный поисковый запрос в поле ввода
                        binding.searchInput.setText(lastRealQuery)
                        binding.searchInput.setSelection(lastRealQuery.length)
                        currentQuery = lastRealQuery
                    } else {
                        // Если не было реального запроса, очищаем поле
                        binding.searchInput.setText("")
                        binding.searchInput.setSelection(0)
                        currentQuery = ""
                        // Показываем пустое состояние
                        showEmptyState()
                        return@setOnClickListener
                    }
                }

                // Выполняем поиск с текущим запросом
                performSearch(currentQuery)
            }
        }

        // Исходное состояние - пустой поиск
        showEmptyState()
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            if (searchHistory.isNotEmpty()) {
                showSearchHistory()
            } else {
                showEmptyState()
            }
            return
        }

        if (isSearching) return

        isSearching = true
        showLoadingState()

        // Добавляем запрос в историю поиска до выполнения запроса
        // Таким образом он будет добавлен независимо от результата
        addToSearchHistory(query)

        // Специальная проверка для симуляции ошибки
        if (query.contains("error", ignoreCase = true)) {
            isSearching = false
            showErrorState("Симулированная ошибка поиска для тестирования")
            return
        }

        // Сохраняем текущий запрос как реальный (не error)
        lastRealQuery = query

        exerciseRepository.searchExercises(
            query = query,
            onSuccess = { exercises ->
                isSearching = false
                Log.d(TAG, "Search results: ${exercises.size} exercises found")
                if (exercises.isEmpty()) {
                    showNoResultsState()
                } else {
                    showResults(exercises)
                    // Убираем отсюда добавление в историю, так как мы уже добавили выше
                    // addToSearchHistory(query)
                }
            },
            onError = { errorMessage ->
                isSearching = false
                Log.e(TAG, "Error in search: $errorMessage")
                showErrorState(errorMessage)
            }
        )
    }

    private fun showLoadingState() {
        binding.emptySearchContainer.visibility = View.GONE
        binding.searchResults.visibility = View.GONE
        binding.searchHistoryContainer.visibility = View.GONE
        binding.searchProgressBar.visibility = View.VISIBLE
    }

    private fun showResults(exercises: List<ExerciseResponse>) {
        binding.emptySearchContainer.visibility = View.GONE
        binding.searchResults.visibility = View.VISIBLE
        binding.searchHistoryContainer.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE

        exerciseAdapter.updateExercises(exercises)
    }

    private fun showEmptyState() {
        binding.emptySearchContainer.visibility = View.VISIBLE
        binding.searchResults.visibility = View.GONE
        binding.searchHistoryContainer.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE

        val emptyStateImage = binding.emptySearchContainer.findViewById<ImageView>(R.id.empty_state_image)
        val emptyStateText = binding.emptySearchContainer.findViewById<TextView>(R.id.empty_state_text)
        val emptyStateDesc = binding.emptySearchContainer.findViewById<TextView>(R.id.empty_state_description)
        val retryButton = binding.emptySearchContainer.findViewById<Button>(R.id.retry_button)

        emptyStateImage.setImageResource(android.R.drawable.ic_menu_search)
        emptyStateText.text = "Что вы хотите найти?"
        emptyStateDesc.text = "Введите запрос для поиска упражнений"
        retryButton.visibility = View.GONE
    }

    private fun showNoResultsState() {
        binding.emptySearchContainer.visibility = View.VISIBLE
        binding.searchResults.visibility = View.GONE
        binding.searchHistoryContainer.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE

        val emptyStateImage = binding.emptySearchContainer.findViewById<ImageView>(R.id.empty_state_image)
        val emptyStateText = binding.emptySearchContainer.findViewById<TextView>(R.id.empty_state_text)
        val emptyStateDesc = binding.emptySearchContainer.findViewById<TextView>(R.id.empty_state_description)
        val retryButton = binding.emptySearchContainer.findViewById<Button>(R.id.retry_button)

        emptyStateImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        emptyStateText.text = "Ничего не найдено"
        emptyStateDesc.text = "Попробуйте изменить запрос"
        retryButton.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding.emptySearchContainer.visibility = View.VISIBLE
        binding.searchResults.visibility = View.GONE
        binding.searchHistoryContainer.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE

        val emptyStateImage = binding.emptySearchContainer.findViewById<ImageView>(R.id.empty_state_image)
        val emptyStateText = binding.emptySearchContainer.findViewById<TextView>(R.id.empty_state_text)
        val emptyStateDesc = binding.emptySearchContainer.findViewById<TextView>(R.id.empty_state_description)
        val retryButton = binding.emptySearchContainer.findViewById<Button>(R.id.retry_button)

        emptyStateImage.setImageResource(android.R.drawable.ic_dialog_alert)
        emptyStateText.text = "Ошибка загрузки"
        emptyStateDesc.text = errorMessage
        retryButton.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текст поискового запроса
        outState.putString("SEARCH_QUERY", currentQuery)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Восстанавливаем текст поискового запроса
        savedInstanceState?.getString("SEARCH_QUERY")?.let {
            binding.searchInput.setText(it)
            // Перемещаем курсор в конец текста
            binding.searchInput.setSelection(it.length)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Удаляем отложенные задачи
        handler.removeCallbacks(searchRunnable)
        _binding = null
    }
}