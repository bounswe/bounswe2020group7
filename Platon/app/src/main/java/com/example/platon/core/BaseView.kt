package com.example.platon.core

interface BaseView<T : BasePresenter> {
    fun setPresenter(presenter: T)
}