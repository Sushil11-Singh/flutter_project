package com.example.productapp.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.R
import com.example.productapp.modal.DatabaseClient
import com.example.productapp.modal.Product
import kotlinx.android.synthetic.main.activity_product_home.*
import java.io.ByteArrayOutputStream


class ProductHomeActivity : AppCompatActivity() {
    private var mUri: Uri? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    var imagePath: String? = null
    var imageInByte: ByteArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_home)

        initViews()
    }

    private fun initViews() {

        btnCreateProject.setOnClickListener {
            createProduct()
        }

        btnShowList.setOnClickListener {
            var intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        layout_capture.setOnClickListener {
            val option = arrayOf("Take from Camera")
            val adapter = ArrayAdapter(
                this,
                android.R.layout.select_dialog_item, option
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle("Select Option")
            builder.setAdapter(adapter,
                DialogInterface.OnClickListener { dialog, which -> // TODO Auto-generated method stub
                    Log.e("Selected Item", which.toString())
                    if (which == 0) {
                        callCamera()
                    }
                })
            val dialog: AlertDialog = builder.create()
            dialog.show();

        }
    }

    private fun callCamera() {
        /* val capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
        *//* if(capturedImage.exists()) {
            capturedImage.delete()
        }*//*
        //capturedImage.createNewFile()
        mUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, "info.camposha.kimagepicker.fileprovider",
                capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)*/

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
        intent.type = "image/*"
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 0)
        intent.putExtra("aspectY", 0)
        intent.putExtra("outputX", 250)
        intent.putExtra("outputY", 200)
    }

    private fun createProduct() {
        var sProductName = etProductName.text.toString()
        var sProductDisc = etProductDesc.text.toString()
        var sProductRegularPrice = etProductRegular.text.toString()
        var sProductSalePrice = etProductSalePrice.text.toString()
        var sProductColor = etProductColor.text.toString()
        var sProductStore = etProductStore.text.toString()

        if (sProductName.isEmpty()) {
            etProductName.setError("Name required");
            etProductName.requestFocus();
            return;
        }

        if (sProductDisc.isEmpty()) {
            etProductDesc.setError("Desc required");
            etProductDesc.requestFocus();
            return;
        }

        if (sProductRegularPrice.isEmpty()) {
            etProductRegular.setError("Regular price required");
            etProductRegular.requestFocus();
            return;
        }

        if (sProductSalePrice.isEmpty()) {
            etProductSalePrice.setError("Sale price required");
            etProductSalePrice.requestFocus();
            return;
        }

        if (sProductColor.isEmpty()) {
            etProductColor.setError("Color required");
            etProductColor.requestFocus();
            return;
        }
        if (sProductStore.isEmpty()) {
            etProductStore.setError("Store required");
            etProductStore.requestFocus();
            return;
        }

        if (ivProductImage.drawable == null) {
            Toast.makeText(this, "Please capture image", Toast.LENGTH_SHORT).show()
        }

        saveInDb(
            sProductName,
            sProductDisc,
            sProductRegularPrice,
            sProductSalePrice,
            sProductColor,
            sProductStore,
            imagePath
        )
    }

    private fun saveInDb(
        sProductName: String,
        sProductDisc: String,
        sProductRegularPrice: String,
        sProductSalePrice: String,
        sProductColor: String,
        sProductStore: String,
        imagePath: String?
    ) {
        class SaveTask() : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {

                var product = Product()
                product.setName(sProductName)
                product.setDescription(sProductDisc)
                product.setRegularPrice(sProductRegularPrice)
                product.setSalePrice(sProductSalePrice)
                product.setColor(sProductColor)
                product.setStores(sProductStore)
                if (imageInByte != null) {
                    product.setProductPhoto(imageInByte!!)
                }

                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .productDao()
                    .insert(product);

                return null
                // ...
            }

            override fun onPreExecute() {
                super.onPreExecute()
                // ...
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                // ...
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        val st = SaveTask()
        st.execute()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    //  val uri = data!!.data
                    val extras = data!!.extras
                    if (extras != null) {
                        val yourImage: Bitmap? = extras.getParcelable("data")
                        // convert bitmap to byte
                        // convert bitmap to byte
                        val stream = ByteArrayOutputStream()
                        yourImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        imageInByte = stream.toByteArray()
                        ivProductImage.setImageBitmap(yourImage)
                    }


                }

        }
    }


}
