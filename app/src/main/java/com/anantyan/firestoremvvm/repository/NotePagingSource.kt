package com.anantyan.firestoremvvm.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.network.HttpException
import com.anantyan.firestoremvvm.data.firestore.list
import com.anantyan.firestoremvvm.data.firestore.paging
import com.anantyan.firestoremvvm.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.io.IOException

class NotePagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, Note>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Note>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Note> {
        return try {
            val position = params.key ?: db.paging()
            val lastDocumentSnapshot = position.documents[position.size() - 1]
            val nextPage = db.list(lastDocumentSnapshot)
            LoadResult.Page(
                data =  position.toObjects(Note::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}