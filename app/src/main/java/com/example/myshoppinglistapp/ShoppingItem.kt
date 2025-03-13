package com.example.myshoppinglistapp

data class ItemModel(
  val id: Int,
  var itemName: String,
  var itemQuantity: String,
  var isEditing: Boolean = false,
)
