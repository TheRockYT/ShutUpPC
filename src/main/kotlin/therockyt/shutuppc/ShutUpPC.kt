import therockyt.shutuppc.error.errors.InvalidHexDigitError
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

const val PORT = 9
fun sendPacket(ipStr: String, macStr: String){
    try {
        val macBytes = getMacBytes(macStr)
        val bytes = ByteArray(6 + 16 * macBytes.size)
        for (i in 0..5) {
            bytes[i] = 0xff.toByte()
        }
        var i = 6
        while (i < bytes.size) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.size)
            i += macBytes.size
        }
        val address = InetAddress.getByName(ipStr)
        val packet = DatagramPacket(bytes, bytes.size, address, PORT)
        val socket = DatagramSocket()
        socket.send(packet)
        socket.close()
    } catch (e: Exception) {
        throw Error("Failed to send hex packet: $e")
    }
}
private fun getMacBytes(macStr: String): ByteArray {
    val bytes = ByteArray(6)
    val hex = macStr.split("(\\:|\\-)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if(hex.size != 6) { throw InvalidHexDigitError() }
    try {
        for (i in 0..5) {
            bytes[i] = hex[i].toInt(16).toByte()
        }
    } catch (e: NumberFormatException) {
        throw InvalidHexDigitError()
    }
    return bytes
}