package com.anantyan.firestoremvvm.utils

import android.util.ArraySet
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

enum class LiveEventConfig {
    /**
     * Supports multi-observers on all cases the same.
     */
    Normal,

    /**
     * Prefer the first observer when user emit the event, register observer, then the `onStart`
     * get called. In this case the _first observer_ will receive the _last event_.
     *
     * This scenario is specially useful when you want to emit the event in the `init` of
     * `ViewModel`, and expect the first observer receive it after `onStart`.
     */
    PreferFirstObserver
}

class LiveEvent<T>(
    private val config: LiveEventConfig = LiveEventConfig.Normal
) : MediatorLiveData<T>() {

    private val observers = ArraySet<ObserverWrapper<in T>>()
    private var hasValueWithoutFirstObserver: Boolean = false

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observers.find { it.observer === observer }?.let { _ -> // existing
            return
        }
        val wrapper = ObserverWrapper(observer)
        if (hasValueWithoutFirstObserver) {
            hasValueWithoutFirstObserver = false
            wrapper.newValue()
        }
        observers.add(wrapper)
        super.observe(owner, wrapper)
    }

    @MainThread
    override fun observeForever(observer: Observer<in T>) {
        observers.find { it.observer === observer }?.let { _ -> // existing
            return
        }
        val wrapper = ObserverWrapper(observer)
        observers.add(wrapper)
        super.observeForever(wrapper)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        if (observer is ObserverWrapper && observers.remove(observer)) {
            super.removeObserver(observer)
            return
        }
        val iterator = observers.iterator()
        while (iterator.hasNext()) {
            val wrapper = iterator.next()
            if (wrapper.observer == observer) {
                iterator.remove()
                super.removeObserver(wrapper)
                break
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        if (config == LiveEventConfig.PreferFirstObserver &&
            observers.isEmpty()
        ) {
            hasValueWithoutFirstObserver = true
        }
        observers.forEach { it.newValue() }
        super.setValue(t)
    }

    private class ObserverWrapper<T>(val observer: Observer<T>) : Observer<T> {

        private var pending = false

        override fun onChanged(value: T) {
            if (pending) {
                pending = false
                observer.onChanged(value)
            }
        }

        fun newValue() {
            pending = true
        }
    }
}