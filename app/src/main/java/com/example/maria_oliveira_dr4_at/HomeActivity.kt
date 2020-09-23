package com.example.maria_oliveira_dr4_at

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.example.maria_oliveira_dr4_at.Classes.CriptoClass
import com.example.maria_oliveira_dr4_at.Classes.InfoAnota
import com.example.maria_oliveira_dr4_at.Classes.ListaAnotaAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class HomeActivity : AppCompatActivity(),
    BillingClientStateListener,
    SkuDetailsResponseListener,
    PurchasesUpdatedListener,
    ConsumeResponseListener {

    private lateinit var clienteInApp: BillingClient
    lateinit var auth: FirebaseAuth
    private var mUser: FirebaseUser? = null
    private var currentSku = "android.test.purchased"
    private val PREF_FILE = "PREF_FILE"
    private lateinit var sharedPref: SharedPreferences
    private var mapSku = HashMap<String,SkuDetails>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        //adiciona o ad
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        btnLogout.setOnClickListener{
            auth .signOut()
            mUser = null
            finish()
        }

        btnNovaAnotacao.setOnClickListener {
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        clienteInApp = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        clienteInApp.startConnection(this)

        checaCompra()
    }


    override fun onDestroy() {
        clienteInApp.endConnection()
        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
        mUser = auth.currentUser
        updateUI()
        setupRcy()

    }

    fun setupRcy(){
        rcyListaAnotacao.adapter = ListaAnotaAdapter(attRcy())
        rcyListaAnotacao.layoutManager =
            LinearLayoutManager(this)
    }
    fun attRcy(): List<InfoAnota> {
        val pathArq = File(this.filesDir.toURI())
        var prefixo = ""
        val dados = mutableListOf<InfoAnota>()
        val files = pathArq.listFiles()

        files?.forEach {
            if ("$prefixo.txt" != it.name && "$prefixo.fig" != it.name) {
                prefixo = it.name.removeSuffix(".txt")
                prefixo = prefixo.removeSuffix(".fig")

                dados.add(getDados(prefixo))
            }
        }

        return dados
    }

    fun getDados(prefixo: String): InfoAnota {
        var removeSuffix = prefixo.removeSuffix(".fig")
        removeSuffix = removeSuffix.removeSuffix(".txt")
        val imagem: ByteArray = CriptoClass().criptoLerImg("$removeSuffix.fig", this)
//        val text: String = CriptoClass().criptoLerTxt("$removeSuffix.txt", this)[1]
//        val tituloDado = prefixo.split("_")[0]
//        val data = prefixo.split("_")[2]
        val text: String = CriptoClass().criptoLerTxt("$removeSuffix.txt", this)[2]
        val tituloDado = prefixo.split("*")[0]
        val data = prefixo.split("*")[1].removeSuffix("*")
        val bitmap = BitmapFactory.decodeByteArray(imagem, 0, imagem.size)


        return InfoAnota(
            tituloDado,
            text,
            data,
            bitmap
        )
    }

    fun updateUI(){
        txtUsuario.text = "USUARIO:${mUser!!.email}"
    }

    override fun onBillingServiceDisconnected() {
        Log.d("COMPRA>>","Serviço InApp desconectado")

    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        if(billingResult?.responseCode ==
            BillingClient.BillingResponseCode.OK){
            Log.d("COMPRA>>","Serviço InApp conectado")
            val skuList = arrayListOf(currentSku)
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(
                BillingClient.SkuType.INAPP)
            clienteInApp.querySkuDetailsAsync(params.build(), this)
        }
    }

    override fun onSkuDetailsResponse(billingResult: BillingResult?,
                                      skuDetailsList: MutableList<SkuDetails>?) {
        if(billingResult?.responseCode ==
            BillingClient.BillingResponseCode.OK){
            mapSku.clear()
            skuDetailsList?.forEach{
                    t ->
                mapSku[t.sku] = t
                val preco = t.price
                val descricao = t.description
                Log.d("COMPRA>>",
                    "Produto Disponivel ($preco): $descricao")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode ==
            BillingClient.BillingResponseCode.OK &&
            purchases != null){

            for (purchase in purchases) {
                GlobalScope.launch (Dispatchers.IO){
                    handlePurchase(purchase)
                }
            }
        }else if (billingResult.responseCode ==
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Log.d("COMPRA JA REALIZADA>>",
                "Produto já foi comprado")

            val userId = auth.currentUser?.uid
            val editor =
                getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
            editor.putBoolean(userId, true)
            editor.commit()

        }else if (billingResult.responseCode ==
            BillingClient.BillingResponseCode.USER_CANCELED){
            Log.d("COMPRA CANCELADA>>",
                "Usuário cancelou a compra")

        }else{
            Log.d("ERRO NA COMPRA>>",
                "Código de erro desconhecido: ${billingResult.responseCode}")
        }

    }

    suspend fun handlePurchase (purchase: Purchase) {
        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED){
            // Aqui acessaria a base e concederia o produto ao usuário
            Log.d("COMPRA>>","Produto obtido com sucesso, reinicie o app para que o ad suma")
            val userId = auth.currentUser?.uid
            val editor =
                getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
            editor.putBoolean(userId, true)
            editor.commit()

            // Acknowledge -> Obrigatório para confirmação ao Google
            if (!purchase.isAcknowledged){
                val acknowledgePurchaseParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO){
                    clienteInApp.acknowledgePurchase(
                        acknowledgePurchaseParams.build())
                }
            }
        }
    }


    private fun checaCompra() {
        val preferences =
            getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        val userId = auth.currentUser?.uid
        val isPurchase = preferences.getBoolean(userId, false)
        if (isPurchase) {
            adView.setVisibility(View.GONE)
            btnRemoveAd.setVisibility(View.GONE)
        }
    }

    override fun onConsumeResponse(billingResult: BillingResult?, string: String?) {
        if (billingResult?.responseCode ==
            BillingClient.BillingResponseCode.OK){
            Log.d( "COMPRA>>" , "Produto Consumido" )
        }
    }

    //consome para comprar novamente
//    fun consumeGoogleInApp (view: View) {
//        var compras = clienteInApp .queryPurchases(
//            BillingClient. SkuType . INAPP )
//        if ( compras. purchasesList . size > 0 )
//        {
//            var purchase: Purchase = compras. purchasesList [ 0 ]
//            var params = ConsumeParams.newBuilder()
//                .setPurchaseToken(purchase. purchaseToken )
//                .setDeveloperPayload(purchase. developerPayload )
//                .build()
//            clienteInApp .consumeAsync(params ,this )
//        }
//    }



    fun processoGoogleInApp(view: View) {
        val skuDetails = mapSku[currentSku]
        val purchaseParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails).build()
        clienteInApp.launchBillingFlow(this, purchaseParams)
    }
}