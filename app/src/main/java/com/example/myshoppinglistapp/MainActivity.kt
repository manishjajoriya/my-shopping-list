package com.example.myshoppinglistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myshoppinglistapp.ui.theme.MyShoppingListAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyShoppingListAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          ShoppingListScreen(innerPadding)
        }
      }
    }
  }
}

@Composable
fun ShoppingListScreen(innerPaddingValues: PaddingValues) {

  var showDialog by remember { mutableStateOf(false) }
  var itemName by remember { mutableStateOf("") }
  var itemQuantity by remember { mutableStateOf("") }

  val itemList = remember { mutableStateListOf<ItemModel>() }

  val top = innerPaddingValues.calculateTopPadding() + 8.dp

  Column(
    modifier = Modifier.padding(top = top).fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Button(onClick = { showDialog = true }) { Text("Add Item") }

    LazyColumn {
      items(itemList) { item ->
        if (!item.isEditing) {
          ColumnItem(
            item,
            onDeleteClick = { itemList -= it },
            onEditClick = {
              val editedItem = itemList.find { it.id == item.id }
              if (editedItem != null) {
                itemList[editedItem.id - 1] = editedItem.copy(isEditing = true)
              }
            },
          )
        } else {
          EditorScreen(
            item,
            onEditComplete = { itemName, itemQuantity ->
              val index = itemList.indexOfFirst { it.id == item.id }
              if (index != -1) {
                itemList[index] =
                  itemList[index].copy(
                    itemName = itemName,
                    itemQuantity = itemQuantity,
                    isEditing = false,
                  )
              }
            },
          )
        }
      }
    }
  }

  if (showDialog) {
    AddItem(
      showDialog = true,
      onShowDialogChange = {
        showDialog = false
        itemQuantity = ""
      },
      itemName = itemName,
      onItemNameChange = { itemName = it },
      itemQuantity = itemQuantity,
      onItemQuantityChange = {
        if (it.toIntOrNull() != null) itemQuantity = it else itemQuantity = "1"
      },
      itemList = itemList,
      onItemListChange = { itemList.add(it) },
    )
  }
}

@Composable
fun AddItem(
  showDialog: Boolean,
  onShowDialogChange: () -> Unit,
  itemName: String,
  onItemNameChange: (String) -> Unit,
  itemQuantity: String,
  onItemQuantityChange: (String) -> Unit,
  itemList: List<ItemModel>,
  onItemListChange: (ItemModel) -> Unit,
) {

  if (showDialog) {
    AlertDialog(
      onDismissRequest = onShowDialogChange,
      confirmButton = {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Button(
            onClick = {
              val add =
                ItemModel(
                  id = itemList.size + 1,
                  itemName = itemName.replaceFirstChar { char -> char.titlecase() },
                  itemQuantity = (itemQuantity.toIntOrNull() ?: "1").toString(),
                )

              onItemListChange(add)
              onItemNameChange("")
              onItemQuantityChange("")

              onShowDialogChange()
            }
          ) {
            Text("Add")
          }

          Button(onClick = onShowDialogChange) { Text("Cancel") }
        }
      },
      title = { Text("Add Item") },
      text = {
        Column {
          OutlinedTextField(
            value = itemName,
            onValueChange = onItemNameChange,
            label = { Text("Item name:") },
            placeholder = { Text("Enter item name") },
            modifier = Modifier.fillMaxWidth(),
          )

          OutlinedTextField(
            value = itemQuantity,
            onValueChange = onItemQuantityChange,
            label = { Text("Item Quantity:") },
            placeholder = { Text("Enter item Quantity") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
          )
        }
      },
    )
  }
}

@Composable
fun ColumnItem(
  itemModel: ItemModel,
  onDeleteClick: (ItemModel) -> Unit,
  onEditClick: (Boolean) -> Unit,
) {

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .border(2.dp, Color.Red, RoundedCornerShape(5.dp)),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text("Name: " + itemModel.itemName, modifier = Modifier.fillMaxWidth(.4f))
    Text("Qty: " + itemModel.itemQuantity)
    IconButton(onClick = { onEditClick(true) }) {
      Icon(Icons.Filled.Edit, contentDescription = "Edit")
    }

    IconButton(onClick = { onDeleteClick(itemModel) }) {
      Icon(Icons.Filled.Delete, contentDescription = "Delete")
    }
  }
}

@Composable
fun EditorScreen(itemModel: ItemModel, onEditComplete: (String, String) -> Unit) {
  var tempItemName by remember { mutableStateOf(itemModel.itemName) }
  var tempItemQuantity by remember { mutableStateOf(itemModel.itemQuantity) }

  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Row(
      modifier = Modifier.padding(4.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
          modifier = Modifier.fillMaxWidth(0.6f),
          value = tempItemName,
          onValueChange = { tempItemName = it },
          label = { Text("Edited Name") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
          modifier = Modifier.fillMaxWidth(0.6f),
          value = tempItemQuantity,
          onValueChange = { tempItemQuantity = it },
          label = { Text("Edited Quantity") },
        )
      }
      Spacer(modifier = Modifier.width(8.dp)) // Add space before the button
      Button(onClick = { onEditComplete(tempItemName, tempItemQuantity) }) { Text("Save") }
    }
  }
}
