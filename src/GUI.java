import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class GUI extends JFrame{
    Path path;
    private DefaultTableModel tableModel;
    private JTable table;
    public GUI() throws HeadlessException {
        setTitle("MSSQL");
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(580, 350));

        JTextArea tablename = new JTextArea(1,10);


        JButton loadButton = new JButton("Select");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!tablename.getText().isBlank()){
                    fetchData(tablename.getText());
                }else {
                    tableModel.addRow(new String[]{"Enter a valid table name"});
                }
            }
        });

        JButton toTxtButton = new JButton("To text file");
        toTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToFile(tablename.getText());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        buttonPanel.add(tablename);
        buttonPanel.add(toTxtButton);

        getContentPane().setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchData(String tablename){
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        try{
            DBConnection connection = new DBConnection();
            ResultSet resultSet = connection.selectAllFromTable(tablename);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++){
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (resultSet.next()){
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++){
                    row[i] = resultSet.getObject(i+1);
                }
                tableModel.addRow(row);
            }
            resultSet.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void writeToFile(String filename){
        path = Paths.get(filename+".txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++){
            for (int j = 0; j < tableModel.getColumnCount(); j++){
                if(tableModel.getValueAt(i,j) != null){
                    sb.append(tableModel.getValueAt(i,j));
                }
                else {
                    sb.append("\7");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        try {
            Files.write(path, sb.toString().getBytes());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
