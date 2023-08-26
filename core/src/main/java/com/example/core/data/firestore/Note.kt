package com.example.core.data.firestore

import com.example.core.data.model.Note
import com.google.firebase.firestore.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

fun FirebaseFirestore.readAll(pageSize: Long = 20): Query{
    return this.collection("notes")
        /*.whereEqualTo("publish", true)*/
        .orderBy("createAt", Query.Direction.DESCENDING)
        .limit(pageSize)
}

suspend fun FirebaseFirestore.read(id: String): Note? {
    return collection("notes")
        .document(id)
        .get()
        .await()
        .toObject(Note::class.java)
}

suspend fun FirebaseFirestore.create(note: Note) {
    val create = this.collection("notes")
        .document()
    val id = create.id
    val item = mapOf(
        "id" to id,
        "title" to note.title,
        "content" to note.content,
        "publish" to note.publish,
        "createAt" to FieldValue.serverTimestamp()
    )
    create.set(item)
        .await()
}

suspend fun FirebaseFirestore.update(id: String, note: Note) {
    this.collection("notes")
        .document(id)
        .update(mapOf(
            "id" to id,
            "title" to note.title,
            "content" to note.content,
            "imgUrl" to note.imgUrl,
            "tags" to note.tags,
            "publish" to note.publish,
            "updateAt" to FieldValue.serverTimestamp()
        ))
        .await()
}

suspend fun FirebaseFirestore.delete(id: String) {
    this.collection("notes")
        .document(id)
        .delete()
        .await()
}