package com.example.maria_oliveira_dr4_at.Classes

import android.content.Context
import android.view.View
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.*

class CriptoClass {

    private fun getEncFile(nome: String, context: Context): EncryptedFile {
        val masterKeyAlias: String =
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val file = File(context.filesDir, nome)
        return EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
            .build()
    }

    fun criptoGravarTxt(nome: String, context: Context, txt : List<String>){
        val encryptedOut: FileOutputStream =
            getEncFile(nome, context).openFileOutput()
        val pw = PrintWriter(encryptedOut)
        txt.forEach{
            pw.println(it)
        }
        pw.flush()
        encryptedOut.close()
    }

    fun criptoLerTxt(nome: String, context: Context) : List<String>{
        val encryptedIn: FileInputStream =
            getEncFile(nome, context).openFileInput()
        val br = BufferedReader(InputStreamReader(encryptedIn))
        val result = mutableListOf<String>()
        br.lines().forEach{
                result.add(it)
        }
            encryptedIn.close()
            return result
    }



    fun criptoGravarImg(nome: String, context: Context, img: ByteArray){
        val encryptedOut: FileOutputStream =
            getEncFile(nome, context).openFileOutput()
//        val pw = PrintWriter(encryptedOut)
        encryptedOut.write(img)
//        pw.flush()
        encryptedOut.close()
    }

    fun criptoLerImg(nome: String, context: Context) : ByteArray{
        val encryptedIn: FileInputStream =
            getEncFile(nome, context).openFileInput()
        val ler = ByteArrayInputStream(encryptedIn.readBytes())
        return ler.readBytes()
    }
}