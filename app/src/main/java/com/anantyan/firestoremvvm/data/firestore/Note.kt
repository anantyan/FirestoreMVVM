package com.anantyan.firestoremvvm.data.firestore

import com.anantyan.firestoremvvm.model.Note
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

suspend fun FirebaseFirestore.paging(): QuerySnapshot {
    return this.collection("notes")
        .whereEqualTo("publish", true)
        .orderBy("createAt")
        .limit(10)
        .get()
        .await()
}

suspend fun FirebaseFirestore.list(paramKey: DocumentSnapshot): QuerySnapshot {
    return this.collection("notes")
        .whereEqualTo("publish", true)
        .orderBy("createAt")
        .startAfter(paramKey)
        .limit(10)
        .get()
        .await()
}

suspend fun FirebaseFirestore.read(id: String): Note? {
    return this.collection("notes")
        .document(id)
        .get()
        .await()
        .toObject(Note::class.java)
}

suspend fun FirebaseFirestore.create(user: FirebaseUser, note: Note) {
    this.collection("notes")
        .add(mapOf(
            "id" to note.id,
            "title" to note.title,
            "content" to note.content,
            "publish" to note.publish,
            "author" to user.uid,
            "createAt" to FieldValue.serverTimestamp()
        ))
        .await()
}

suspend fun FirebaseFirestore.update(user: FirebaseUser, id: String, note: Note) {
    this.collection("notes")
        .document(id)
        .update(mapOf(
            "id" to note.id,
            "title" to note.title,
            "content" to note.content,
            "imgUrl" to note.imgUrl,
            "tags" to note.tags,
            "publish" to note.publish,
            "author" to user.uid,
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