package com.bitmark.sampleblockstore

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitmark.sampleblockstore.ui.theme.SampleBlockStoreTheme
import com.google.android.gms.auth.blockstore.Blockstore
import com.google.android.gms.auth.blockstore.BlockstoreClient
import com.google.android.gms.auth.blockstore.RetrieveBytesRequest
import com.google.android.gms.auth.blockstore.RetrieveBytesResponse
import com.google.android.gms.auth.blockstore.StoreBytesData
import java.util.UUID


class MainActivity : ComponentActivity() {
    private val data = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBlockStoreData()

        setContent {
            SampleBlockStoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        DataValue(data.value, Modifier.align(Alignment.Center))
                        SetButton(
                            "Random BlockStore Data", Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            saveBlockStoreData(UUID.randomUUID().toString())
                        }
                    }
                }
            }
        }
    }

    private fun getBlockStoreData() {
        val client = Blockstore.getClient(this)

        val retrieveRequest = RetrieveBytesRequest.Builder()
            .setKeys(listOf(BlockstoreClient.DEFAULT_BYTES_DATA_KEY))
            .build()
        client.retrieveBytes(retrieveRequest)
            .addOnSuccessListener { result: RetrieveBytesResponse ->
                val blockstoreDataMap = result.blockstoreDataMap
                val defaultBytesData = blockstoreDataMap[BlockstoreClient.DEFAULT_BYTES_DATA_KEY]

                if (defaultBytesData != null) {
                    data.value = String(defaultBytesData.bytes)
                }
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(ContentValues.TAG, "Failed to store bytes", e)
            }
    }

    private fun saveBlockStoreData(value: String) {
        val client = Blockstore.getClient(this)

        val storeRequest1 = StoreBytesData.Builder()
            .setBytes(value.toByteArray()) // Call this method to set the key value with which the data should be associated with.
            .build()
        client.storeBytes(storeRequest1)
            .addOnSuccessListener { result: Int ->
                Log.d(ContentValues.TAG, "Stored $result bytes")
                data.value = value
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Failed to store bytes", e)
            }
    }
}

@Composable
fun DataValue(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Data: $name",
        modifier = modifier
    )
}

@Composable
fun SetButton(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = name)
    }
}