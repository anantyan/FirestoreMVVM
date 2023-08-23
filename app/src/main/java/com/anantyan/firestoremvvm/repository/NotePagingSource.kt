package com.anantyan.firestoremvvm.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.anantyan.firestoremvvm.data.firestore.readAll
import com.anantyan.firestoremvvm.model.Note
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class NotePagingSource(
    private val db: FirebaseFirestore
) : PagingSource<DocumentSnapshot, Note>() {
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Note>): DocumentSnapshot? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Note> {
        return try {
            val query = if (params.key == null) {
                db.readAll().limit(params.loadSize.toLong())
            } else {
                db.readAll().startAfter(params.key).limit(params.loadSize.toLong())
            }

            // coba baca dari cache terlebih dahulu
            var snapshot = query.get(Source.CACHE).await()

            // jika data dari cache kosong atau tidak lengkap, ambil dari server
            if (snapshot.documents.isEmpty() || snapshot.size() < params.loadSize) {
                snapshot = try {
                    query.get(Source.DEFAULT).await()
                } catch (e: Exception) {
                    query.get(Source.CACHE).await()
                }
            }

            val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }

            val nextKey = if (notes.isEmpty()) {
                null
            } else {
                snapshot.documents[snapshot.size() - 1]
            }

            LoadResult.Page(
                data =  notes,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}