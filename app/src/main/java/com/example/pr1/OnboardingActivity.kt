package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.pr1.databinding.ActivityOnboardingBinding
import com.example.pr1.databinding.ItemOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val onboardingItems = mutableListOf<OnboardingItem>()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingItems()
        setupViewPager()
        setupButtons()
    }

    private fun setupOnboardingItems() {
        onboardingItems.add(
            OnboardingItem(
                "Медитация для спокойствия",
                "Найдите внутреннее спокойствие с нашими ежедневными медитациями",
                android.R.drawable.ic_menu_compass
            )
        )
        onboardingItems.add(
            OnboardingItem(
                "Улучшите свой сон",
                "Спите лучше с нашими сонными историями и звуками природы",
                android.R.drawable.ic_menu_slideshow
            )
        )
        onboardingItems.add(
            OnboardingItem(
                "Снизьте уровень стресса",
                "Управляйте стрессом и тревогой с помощью дыхательных упражнений",
                android.R.drawable.ic_menu_view
            )
        )
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
            // Устанавливаем индикатор для каждой вкладки
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                updateButtonText()
            }
        })
    }

    private fun setupButtons() {
        binding.skipButton.setOnClickListener { navigateToMainActivity() }

        binding.nextButton.setOnClickListener {
            if (currentPosition < onboardingItems.size - 1) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                navigateToMainActivity()
            }
        }

        updateButtonText()
    }

    private fun updateButtonText() {
        if (currentPosition == onboardingItems.size - 1) {
            binding.nextButton.setText(R.string.action_continue)
        } else {
            binding.nextButton.setText(R.string.action_next)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    data class OnboardingItem(
        val title: String,
        val description: String,
        val imageResId: Int
    )

    inner class OnboardingAdapter(private val items: List<OnboardingItem>) :
        RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
            val binding = ItemOnboardingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return OnboardingViewHolder(binding)
        }

        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
            val item = items[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int = items.size

        inner class OnboardingViewHolder(private val binding: ItemOnboardingBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: OnboardingItem) {
                binding.title.text = item.title
                binding.description.text = item.description
                binding.image.setImageResource(item.imageResId)
            }
        }
    }
}