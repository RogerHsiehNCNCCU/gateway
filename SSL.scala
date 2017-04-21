package com.oring.smartcity.example

import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{SSLContext, TrustManagerFactory}


class SSL {
  def SSL() ={
    
  }
  def getSSLcontext(key : String, ks : String) : SSLContext ={
    val pw = key
    
    val sslContext = SSLContext.getInstance("SSL")
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    val keyStore : KeyStore = KeyStore.getInstance("JKS", "SUN")
    keyStore.load( new FileInputStream ( ks ),    pw.toCharArray())
    trustManagerFactory.init(keyStore)
    sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom())
    
    return sslContext
  }
}