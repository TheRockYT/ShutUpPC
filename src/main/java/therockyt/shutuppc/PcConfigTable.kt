package therockyt.shutuppc

import therockyt.shutuppc.utils.FileUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PcConfigTable(val guiinterface: GuiInterface) {
    val file = File("storage","saved.txt")
    val pcs = ArrayList<PC>()
    fun savePCsToFile(){
        val content = ArrayList<String>()
        for(pc in pcs){
            content.add("${pc.ip};${pc.mac};${pc.note}")
        }
        FileUtils.setFileContent(file, content)
    }
    fun loadPCsTableFromFile(){
        val content = FileUtils.getFileContent(file)
        for(str in content){
            val split = str.split(";")
            if(split.size > 2){
                pcs.add(PC(PCState.WAITING, -1, split[0], split[1], split[2]))
            }
        }
        guiinterface.updatePcTable(pcs)
    }
    fun deletePC(index: Int){
        pcs.removeAt(index)
        savePCsToFile()
    }

    fun updateTable() {
        guiinterface.updatePcTable(pcs)
    }
    fun addPC(ip: String, mac: String, note: String){
        pcs.add(PC(PCState.WAITING, -1, ip, mac, note))
        savePCsToFile()
        updateTable()
    }
    fun updateTable(index: Int) {
        val pc = pcs[index]
        guiinterface.tablemodel!!.setValueAt(pc.state, index, 0)
        if(pc.last_ping > -1){
            val pattern = " HH:mm:ss"
            val simpleDateFormat = SimpleDateFormat(pattern)
            val date = simpleDateFormat.format(Date(pc.last_ping))
            guiinterface.tablemodel!!.setValueAt(date, index, 1)
        }else{
            guiinterface.tablemodel!!.setValueAt("-", index, 1)
        }
        guiinterface.tablemodel!!.setValueAt(pc.ip, index, 2)
        guiinterface.tablemodel!!.setValueAt(pc.mac, index, 3)
        guiinterface.tablemodel!!.setValueAt(pc.note, index, 4)
    }

    fun editPC(index: Int, ip: String, mac: String, note: String) {
        val pc = pcs[index]
        pc.ip = ip
        pc.mac = mac
        pc.note = note
        savePCsToFile()
        updateTable()
    }
}