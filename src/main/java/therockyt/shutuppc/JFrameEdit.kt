package therockyt.shutuppc

import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel


class JFrameEdit(val pctable: PcConfigTable?, val index: Int?, val pc: PC?) {
    var frame: JFrame? = null
    var tablemodel: DefaultTableModel? = null
    val edit = index != null
    fun create(){
        if(edit){

            frame = JFrame("Edit - ShutUpPC by TheRockYT")
        }else{

            frame = JFrame("Add - ShutUpPC by TheRockYT")
        }
        frame!!.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame!!.isResizable = false
        val dim: Dimension = Toolkit.getDefaultToolkit().screenSize
        frame!!.setLocation((dim.width - frame!!.size.width) / 2, (dim.height - frame!!.size.height) / 2)
        frame!!.layout = GridLayout()
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        val headingPanel = JPanel()
        var pctext = "Add PC"
        if(edit){
            pctext = "Edit PC"
        }
        val headingLabel = JLabel(pctext)
        headingPanel.add(headingLabel)
        val panel = JPanel(GridBagLayout())
        val constr = GridBagConstraints()
        constr.insets = Insets(5, 5, 5, 5)
        constr.anchor = GridBagConstraints.WEST
        constr.gridx = 0
        constr.gridy = 0
        val nameLabel = JLabel("IP:")
        val emailLabel = JLabel("MAC:")
        val phoneLabel = JLabel("NOTE:")
        val nameInput = JTextField(20)
        val emailInput = JTextField(20)
        val phoneInput = JTextField(20)
        if(edit){
            nameInput.text = pc!!.ip
            emailInput.text = pc.mac
            phoneInput.text = pc.note
        }
        panel.add(nameLabel, constr)
        constr.gridx = 1
        panel.add(nameInput, constr)
        constr.gridx = 0
        constr.gridy = 1
        panel.add(emailLabel, constr)
        constr.gridx = 1
        panel.add(emailInput, constr)
        constr.gridx = 0
        constr.gridy = 2
        panel.add(phoneLabel, constr)
        constr.gridx = 1
        panel.add(phoneInput, constr)
        constr.gridx = 0
        constr.gridy = 4
        constr.gridwidth = 2
        constr.anchor = GridBagConstraints.CENTER
        val button = JButton("Done")
        button.addActionListener {
            if(edit){

                pctable!!.editPC(index!!, nameInput.text, emailInput.text, phoneInput.text)
            }else{
                pctable!!.addPC(nameInput.text, emailInput.text, phoneInput.text)
            }
            frame!!.dispose()
        }
        panel.add(button, constr)
        mainPanel.add(headingPanel)
        mainPanel.add(panel)
        frame!!.add(mainPanel)
        frame!!.pack()
        frame!!.setSize(300, 250)
        frame!!.setLocationRelativeTo(null)
        frame!!.isVisible = true
    }
}