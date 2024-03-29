package therockyt.shutuppc

import com.google.gson.JsonParser
import sendPacket
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.util.stream.Collectors
import javax.swing.*
import javax.swing.event.ListSelectionListener
import javax.swing.table.DefaultTableModel
import kotlin.system.exitProcess


class GuiInterface : ActionListener, KeyAdapter() {
    val version = "1.0.3"
    private var frame = JFrame("ShutUpPC by TheRockYT")
    private var jtable: JTable? = null
    private var pctable: PcConfigTable? = null
    var tablemodel: DefaultTableModel? = null
    fun setAction(s: String?){
        if(s == null){
            frame.title = "ShutUpPC by TheRockYT"
        }else{
            frame.title = "$s - ShutUpPC by TheRockYT"
        }
    }
    fun create(){
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isResizable = false
        val dim: Dimension = Toolkit.getDefaultToolkit().screenSize
        frame.setSize(1080, 720)
        frame.setLocation((dim.width - frame.size.width) / 2, (dim.height - frame.size.height) / 2)
        frame.layout = GridLayout()
        val menuBar = JMenuBar()
        val pc = JMenu("PC")
        val add = JMenuItem("Add")
        val start = JMenuItem("Wake-on-LAN")
        val edit = JMenuItem("Edit")
        val delete = JMenuItem("Delete")
        add.addActionListener(this)
        start.addActionListener(this)
        edit.addActionListener(this)
        delete.addActionListener(this)
        pc.add(add)
        pc.add(start)
        pc.add(edit)
        pc.add(delete)
        menuBar.add(pc)
        val process = JMenu("Process")
        val about = JMenuItem("About")
        val quit = JMenuItem("Quit")
        val updates = JMenuItem("Check for Updates (API)")
        about.addActionListener(this)
        quit.addActionListener(this)
        updates.addActionListener(this)
        process.add(about)
        process.add(quit)
        process.add(updates)
        menuBar.add(process)
        val strArr = ArrayList<String>()
        strArr.add("STATE")
        strArr.add("LAST PINGED")
        strArr.add("IP")
        strArr.add("MAC")
        strArr.add("NOTE")
        tablemodel = DefaultTableModel(emptyArray(),strArr.toArray())
        jtable = JTable(tablemodel)
        jtable!!.showHorizontalLines = true
        jtable!!.rowSelectionAllowed = true
        jtable!!.dragEnabled = false
        jtable!!.setDefaultEditor(Any::class.java, null)
        jtable!!.isEnabled = true


        val select = jtable!!.selectionModel
        select.addListSelectionListener(ListSelectionListener {
            jtable!!.changeSelection(jtable!!.selectedRow, jtable!!.selectedColumn, false, false)
        })
        jtable!!.revalidate()
        jtable!!.repaint()
        val sp = JScrollPane(jtable!!)
        frame.add(sp)

        frame.jMenuBar = menuBar
        pctable = PcConfigTable(this)
        pctable!!.loadPCsTableFromFile()
        frame.isVisible = true
        frame.addKeyListener(this)
        val ns = NetworkScanner(pctable!!)
        Thread(Runnable {
            while(true){
                Thread.sleep(5000)
                ns.scanPCs()
            }
        }).start()
    }
    fun updatePcTable(pcs: ArrayList<PC>){
        jtable!!.clearSelection()
        tablemodel!!.rowCount = 0
        for(pc in pcs){
            val data = ArrayList<String>()
            data.add(pc.state.toString())
            if(pc.last_ping > -1){
                data.add(pc.last_ping.toString())
            }else{
                data.add("-")
            }
            data.add(pc.ip)
            data.add(pc.mac)
            data.add(pc.note)
            tablemodel!!.addRow(data.toArray())
        }
    }
    fun deleteCurrentPC(){
        val row = jtable!!.selectedRow
        if(row > -1){
           pctable!!.deletePC(row)
           tablemodel!!.removeRow(row)
       }else{
            JOptionPane.showMessageDialog(frame, "Delete failed!\nNo PC selected!", "ShutUpPC - Error", JOptionPane.ERROR_MESSAGE)
        }
    }
    fun checkForUpdates(){
        setAction("Checking for updates...")
        try {
            val content = getURLContent(URL("https://www.therockyt.com/api/shutuppc/versions.json"))
            val jsonElement = JsonParser().parse(content)
            val jsonObject = jsonElement.asJsonObject
            val latest = jsonObject["latest-release"].asString
            val latest_development = jsonObject["latest-development"].asString
            val current = jsonObject["version"].asJsonObject[version].asJsonObject
            var msg = "You are using ShutUpPC v$version"
            var warn = false
            if(version.equals(latest, true)){
                msg += "\nThe latest release"
            }else
            if(version.equals(latest_development, true)){
                msg += "\nThe latest development version"
            }else{

                if(current["development"].asBoolean){
                    msg += "\nAn outdated development version."
                    warn = true
                }else{
                    msg += "\nAn outdated release."
                }
                msg += "\n(latest release: v$latest/latest development: $latest_development)"
                if(current["update"].asBoolean){
                    msg += "\nAn update is recommended."
                    warn = true
                }
                msg += "\nUpdate at https://www.therockyt.com/shutuppc."
            }
            if(warn){

                JOptionPane.showMessageDialog(frame, msg, "ShutUpPC - Update", JOptionPane.WARNING_MESSAGE)
            }else{
                JOptionPane.showMessageDialog(frame, msg, "ShutUpPC - Update", JOptionPane.INFORMATION_MESSAGE)

            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(frame, "ShutUpPC failed to load updates.\n${e.message}", "ShutUpPC - Update failed", JOptionPane.ERROR_MESSAGE)
        }
        setAction(null)
    }

    @Throws(Exception::class)
    fun getURLContent(url: URL): String? {
        val connection = url.openConnection()
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        val `is` =  connection.getInputStream()
        val reader = BufferedReader(InputStreamReader(`is`))
        var lines: String? = ""
        var line: String? = null
        while (reader.readLine().also { line = it } != null) {
            lines += line
        }
        reader.close()
        return lines
    }
    fun wakeCurrentPC(){
        val row = jtable!!.selectedRow
        if(row > -1){
            val pc = pctable!!.pcs[row]
            try {
                sendPacket(pc.ip, pc.mac)
                JOptionPane.showMessageDialog(frame, "Wake-on-LAN sent!", "ShutUpPC - Wake-on-LAN", JOptionPane.INFORMATION_MESSAGE)
            } catch (e: Error){
                JOptionPane.showMessageDialog(frame, "Wake-on-LAN failed!\n$e", "ShutUpPC - Error", JOptionPane.ERROR_MESSAGE)
            }
        }else{
            JOptionPane.showMessageDialog(frame, "Wake-on-LAN failed!\nNo PC selected!", "ShutUpPC - Error", JOptionPane.ERROR_MESSAGE)
        }
    }
    override fun actionPerformed(e: ActionEvent?) {
        if (e != null) {
            val src = e.source
            if(src is JMenuItem){
                if(src.text.equals("Quit")){
                    exitProcess(0)
                }
                if(src.text.equals("About")){
                    JOptionPane.showMessageDialog(frame, "ShutUpPC by TheRockYT\nWebsite: https://www.therockyt.com\nVersion: v$version", "ShutUpPC - About", JOptionPane.INFORMATION_MESSAGE)
                }
                if(src.text.equals("Delete")){
                    deleteCurrentPC()
                }
                if(src.text.equals("Wake-on-LAN")){
                    wakeCurrentPC()
                }
                if(src.text.equals("Add")){
                    JFrameEdit(pctable, null, null).create()
                }
                if(src.text.equals("Edit")){
                    val row = jtable!!.selectedRow
                    if(row > -1){
                        val pc = pctable!!.pcs[row]
                        JFrameEdit(pctable, row, pc).create()
                    }else{
                        JOptionPane.showMessageDialog(frame, "Edit failed!\nNo PC selected!", "ShutUpPC - Error", JOptionPane.ERROR_MESSAGE)
                    }
                }
                if(src.text.equals("Check for Updates (API)")){
                    checkForUpdates()
                }
            }
        }
    }

    override fun keyPressed(event: KeyEvent) {
        val keyCode = event.keyCode
        println("Key Pressed: $keyCode")
        if (keyCode == KeyEvent.VK_ESCAPE) {
            exitProcess(0)
        }
        if (keyCode == KeyEvent.VK_DELETE) {
            deleteCurrentPC()
        }
    }

    override fun keyReleased(event: KeyEvent) {}
}