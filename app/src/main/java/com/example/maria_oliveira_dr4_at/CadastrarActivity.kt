package com.example.maria_oliveira_dr4_at

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.example.maria_oliveira_dr4_at.Classes.CriptoClass
import kotlinx.android.synthetic.main.activity_cadastrar.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CadastrarActivity : AppCompatActivity() {
    private var imgBArray: ByteArray? = null
    private var lat: String = ""
    private var lon: String = ""
    val REQUEST_PERMISSIONS_CODE = 666
    val REQUEST_CAPTURE_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar)

        imgAnota.setOnClickListener{

            val pictureIntent = Intent (MediaStore.ACTION_IMAGE_CAPTURE)

            if (pictureIntent.resolveActivity(packageManager)!= null){
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
            }
        }
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CAPTURE_IMAGE &&
            resultCode == Activity.RESULT_OK
        ) {
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!["data"] as Bitmap?
                imgAnota.setImageBitmap(imageBitmap)

                val streamOutput = ByteArrayOutputStream()

                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamOutput)
                }
                val byteArray = streamOutput.toByteArray()
                imgBArray = byteArray
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //GPS (prioridade) ou sinal de celular para achar a localização
    private fun getCurrentCoordinates() {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
        val isNetworkEnabled = locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.d("Permissao", "Ative os serviços necessários")
        } else {
            if (isGPSEnabled) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        30000L, 0f, locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.d("Permissao", "Erro de permissão")
                }
            } else if (isNetworkEnabled) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L, 0f, locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.d("Permissao", "Erro de permissão")
                }
            }
        }
    }


    //pega a localização
    private val locationListener: LocationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                lat = "${location.latitude}"
                lon = "${location.longitude}"
                userLocal.text = "${lat} ${lon}"

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    //identifica a permissão


    fun saveNoteCLocal(view: View?) {
        val permissionAFL = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionACL = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissionAFL != PackageManager.PERMISSION_GRANTED &&
            permissionACL != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                callDialog(
                    "É preciso permitir acesso à localização!",
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_CODE
                )
            }
        } else {
            getCurrentCoordinates()
            previewAnotaCripto()
        }
    }

    fun previewAnotaCripto(){
        if (!edtTitulo.text.isNullOrEmpty() && imgAnota != null && !edtAnota.text.isNullOrEmpty()) {
            txtPreview.visibility = View.VISIBLE
            pvwCard.visibility = View.VISIBLE
            val titulo = edtTitulo.text.toString()
            val anotacao = edtAnota.text.toString()
            pvwTitulo.text = titulo
            pvwAnota.text = anotacao
            pvwImg.setImageBitmap(imgAnota.drawToBitmap())
            val today = Calendar.getInstance().time //pega data
            val formatter = SimpleDateFormat("dd.MM.yyyy") //formata data
            val date = "${formatter.format(today)}"
            pvwData.setText(date)

            val dataInsert = SimpleDateFormat("dd.MM.yyyy").format(Date())
            val data = pvwData.setText(date)
            val nomeFile = "${titulo.toUpperCase(Locale.ROOT)}*${dataInsert}*"
            val nomeTxt = "$nomeFile.txt"
            val nomeImg = "$nomeFile.fig"
            CriptoClass().criptoGravarTxt(
                nomeTxt,
                this,
                listOf(lat, lon, anotacao)
                )
            CriptoClass().criptoGravarImg(
                nomeImg,
                this,
                imgBArray!!
            )
        } else {
            Toast.makeText(
                this,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    //método utilitário
    private fun callDialog(
        mensagem: String,
        permissions: Array<String>
    ) {
        var mDialog = AlertDialog.Builder(this)
            .setTitle("Permissão")
            .setMessage(mensagem)
            .setPositiveButton("Ok")
            { dialog, id ->
                ActivityCompat.requestPermissions(
                    this@CadastrarActivity, permissions,
                    REQUEST_PERMISSIONS_CODE
                )
                dialog.dismiss()
            }
            .setNegativeButton("Cancela")
            { dialog, id ->
                dialog.dismiss()
            }
        mDialog.show()
    }

    fun retornarActvity(view: View) {
        finish()
    }

}