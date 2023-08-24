package com.anantyan.firestoremvvm.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.anantyan.firestoremvvm.data.firestore.readAll
import com.anantyan.firestoremvvm.model.Note
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class NotePagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, Note>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Note>): QuerySnapshot? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Note> {
        return try {
            val currentPage = params.key ?: db.readAll(15).get().await()

            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val lastVisibleNote = currentPage.documents.lastOrNull()
            val nextPage = lastVisibleNote?.let { db.readAll(15).startAfter(it).get().await() }

            LoadResult.Page(
                data =  currentPage.toObjects(Note::class.java),
                prevKey = null,
                nextKey = if (nextPage?.isEmpty != false) null else nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}