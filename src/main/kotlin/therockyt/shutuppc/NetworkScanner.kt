package therockyt.shutuppc

import therockyt.shutuppc.error.errors.HostOfflineError
import therockyt.shutuppc.error.errors.PingError

class NetworkScanner(val pctable: PcConfigTable) {
    fun scanPCs(){
        for((i, pc) in pctable.pcs.withIndex()){
            val t = Thread(pingThread(pc, pctable, i))
            t.start()
        }
    }
    class pingThread(private val pc: PC, private val pctable: PcConfigTable, private val index: Int): Runnable {
        override fun run() {
            try {
                sendPingRequest(pc.ip)
                pc.state = PCState.ONLINE
                pc.last_ping = System.currentTimeMillis()
            }catch (e: HostOfflineError){
                pc.state = PCState.OFFLINE
            }catch (e: PingError){
                pc.state = PCState.ERROR
            }
            try {


                pctable.updateTable(index)
            } catch (_: Exception){

            }
        }
    }
}