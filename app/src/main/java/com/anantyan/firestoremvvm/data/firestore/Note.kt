package com.anantyan.firestoremvvm.data.firestore

import com.anantyan.firestoremvvm.model.Note
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

fun FirebaseFirestore.readAll(): Query{
    return this.collection("notes")
        /*.whereEqualTo("publish", true)*/
        .orderBy("createAt")
}

suspend fun FirebaseFirestore.read(id: String): Note? {
    return collection("notes")
        .document(id)
        .get(Source.CACHE)
        .await()
        .toObject(Note::class.java) ?: collection("notes")
        .document(id)
        .get(Source.SERVER)
        .await()
        .toObject(Note::class.java)
}

suspend fun FirebaseFirestore.create(note: Note) {
   this.runTransaction {
       val create = this.collection("notes").document()
       val id = create.id
       val item = mapOf(
           "id" to id,
           "title" to note.title,
           "content" to note.content,
           "publish" to note.publish,
           "createAt" to FieldValue.serverTimestamp()
       )
       it.set(create, item)
       null
   }.await()
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