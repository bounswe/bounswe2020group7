package com.cmpe451.platon.core

interface BaseView<T : BasePresenter> {
    fun initializePresenter()
}