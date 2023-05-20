package therockyt.shutuppc

import therockyt.shutuppc.error.Error
import therockyt.shutuppc.error.errors.HostOfflineError
import therockyt.shutuppc.error.errors.PingError
import java.net.InetAddress
import java.net.NetworkInterface

@Throws(HostOfflineError::class)
fun getScanRange(): ArrayList<String>{

    val addr = try{
        InetAddress.getLocalHost().address
    } catch (e: Exception){
        throw HostOfflineError("localhost")
    }
    val array = ArrayList<String>()
    for (i in 1..254) {
        addr[3] = i.toByte()
        val address = InetAddress.getByAddress(addr)
        val ipStr = address.hostAddress
        array.add(ipStr)
    }
    return array
}

@Throws(PingError::class, HostOfflineError::class)
fun sendPingRequest(ipAddress: String): Long {
    try {
        val inetAddress = InetAddress.getByName(ipAddress)
        val start = System.currentTimeMillis()
        if(inetAddress.isReachable(5000)){
            return System.currentTimeMillis() - start
        }
    } catch (e: Exception){
        throw PingError(ipAddress, e.toString())
    }
    throw HostOfflineError(ipAddress)
}

// Not Working
@Throws(PingError::class, HostOfflineError::class)
fun getMacAddress(): String {
    try {
        val inetAddress = InetAddress.getLocalHost()
        try {
            val networkInterface = NetworkInterface.getByInetAddress(inetAddress)
            val macAddress = networkInterface.hardwareAddress
            return formatMacAddress(macAddress)
        }catch (e: Exception){
            e.printStackTrace()
        }
    } catch (e: Exception){
        throw PingError("localhost", e.toString())
    }
    throw HostOfflineError("localhost")
}
fun formatMacAddress(macAddress: ByteArray): String{
    val hexadecimal = arrayOfNulls<String>(macAddress.size)
    for (i in macAddress.indices) {
        hexadecimal[i] = java.lang.String.format("%02X",macAddress[i])
    }
    return java.lang.String.join("-", *hexadecimal)
}