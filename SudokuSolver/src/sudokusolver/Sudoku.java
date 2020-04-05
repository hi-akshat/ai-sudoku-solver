/*
ALGORITHMIC APPROACH USED:
While solving and developing this code for the problem, I have used both constraint satisfaction along and 
algorithmic backtracking to solve the Sudoku.

Created a GUI for the sudoku board. I have used an array of JTextField of size 9*9 to navigate around this problem. 
Also, user ha the flexibility to input via entering manually from the gui or entering input through a csv file.

Once the user hits the solve button:
***SUDOKU BOARD IS CREATED FROM THE INPUT ON THE GUI BOARD
***CHECK IF THE INPUTS ON THE GUI BOARD IS CORRECT OR NOT, IF THEY ARE CORRECT AND THE SUDOKU IS SOLVED, 
***ELSE IF THE INPUTS ARE INVALID, AN ERROR MESSAGE IS DISPLAYED. 
***IF SUDOKU IS SOLVED SUCCESSFULLY, DISPLAY THE SUCCESS MESSAGE. ELSE, DISPLAY NO SOLUTION.

Constraint Satisafaction Algorithm:
***METHOD TO CHECK IF THE DIGIT TO BE FILLED IN CELL IS ALREADY PRESENT IN A ROW/COLUMN/GRID
***FIRST CHECKING ROW CONSTARINT – IF GIVEN NO. IS ALREADY PRESENT IN ROW OR NOT
***THEN CHECKING COLUMN CONSTARINT - IF GIVEN NO. IS ALREADY PRESENT IN COLUMN OR NOT
***THEN CHECKING GRID CONSTARINT - IF GIVEN NO. IS ALREADY PRESENT IN GRID OR NOT
***IF A CLASH IS FOUND, THEN RETURN TRUE, ELSE RETURN FALSE

Main sudoku solving algortithm using the above mentioned constraint satisfacion algorithm:
***INITIALIZED A BOOLEAN FLAG1 VARIABLE AS FALSE.
***INITIALIZED VARIABLES ROW AND COL BOTH AS 0.
***FINDING UNASSIGNED LOCATION ON SUDOKU BOARD
***IF AN EMPTY PLACE IS FOUND ON THE BOARD, THEN BREAK OUT OF THE LOOP
***IF NO EMPTY PLACE IS FOUND, THEN RETURN TRUE AND SUDOKU SOLUTION IS COMPLETED
***IF EMPTY SPACE IS FOUND, 
***CHECKING ALL THE NUMBERS FROM 1-9 TO B FILLED IN THE EMPTY SPACE
***CHECKING IF THE NUMBER TO BE FILLED CAN BE PLACED IN THE EMPTY POSITION WITHOUT ANY CLASHES (ROW, COLUMN OR GRID)
***COSTRAINT SATISFACTION ALGORITHM IMPLEMENTED
***SETTING THE EMPTY VALUE TO THE CURRENT NUMBER BEING USED IN THE ITERATION
***SETTING THIS NUMBER ON THE SUDOKU BOARD IN GUI
***USING THREAD.SLEEP() TO DISPLAY THE BACKTRACKING ON SUDOKU BOARD IN GUI
***RETURN TRUE IF SUCCESSFUL
***IF FAILING, THEN TRYING AGAIN BY SETTING GRID POSITION AS 0
***PERFORMING BACKTRACKING BY RETURNING FALSE

*/

package sudokusolver;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author akshat
 */

//CUSTOM CLASS TO MODIFY JTEXTFIELD PROPERTIES
class mod_tf extends JTextField{

    Font f = new Font("Tahoma",Font.BOLD,25);
    mod_tf(){
    this.setFont(f);
    this.setHorizontalAlignment(JTextField.CENTER);
    }
    
}

class Sudoku implements ActionListener{
    
    //DECLARING STATIC VARIABLES
    static int[][] grid = new int[9][9] ;
    static boolean flag = false;
    static mod_tf[][] tf_sudoku = new mod_tf[9][9];
    static JFrame f;
    static JButton b1,b2,b3;
    static int iter=0;
    static long start;
    static long end;
    JPanel p1,p2;
    
    //DESIGNING GUI FOR THE SUDOKU
    public Sudoku()
    {
        f = new JFrame("SUDOKU");
        p1 = new JPanel();
        p1.setLayout(null);
        p2 = new JPanel(new GridLayout(9,9));           
        
        for(int i = 0 ; i <9; i++ ){
            for(int j = 0 ; j <9; j++ )
            {
                tf_sudoku[i][j] = new mod_tf();
                p2.add(tf_sudoku[i][j]);
            }
        }
        b1 = new JButton("SOLVE");
        b1.addActionListener(this);
        b2 = new JButton("RESET");
        b2.addActionListener(this);
        b3 = new JButton("INPUT FROM CSV FILE");
        b3.addActionListener(this);
        b2.addActionListener(this);
        b1.setBounds(0, 502, 166, 30);
        b2.setBounds(166, 502, 166, 30);
        b3.setBounds(166*2, 502, 166, 30);
        p1.add(b1);
        p1.add(b2);
        p1.add(b3);
        p2.setBounds(0, 0, 500, 500);
        p1.add(p2);
        f.add(p1);
        
        //FILL THE COLOURING OF GRIDS DIFFERENTLY 
        for(int i=0 ; i<3; i++ ){
            for(int j=0 ; j<3 ; j++ ){
                if( (i+j) % 2 != 0 ){
                   
                    int r = i*(3) ;
                    int c = j*(3) ;
                   
                    for(int x = 0 ; x < 3 ; x++){
                        for(int y = 0 ; y <3 ; y++){
                           tf_sudoku[(r+x)][(c+y)].setBackground(Color.LIGHT_GRAY);
                       }
                    }
                   
                }
            }
        }
        
        f.setSize(515,578);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e)
    {
        
        //EXECUTION OF SOLVE BUTTON
        if( e.getSource() == b1)
        {
            
            //TO RUN THREADS IN BACKGROUND AND STILL MAKE GUI RESPONSIVE
            
            SwingWorker<String,String> swingworker = new SwingWorker<String, String>(){
                @Override
                protected String doInBackground() throws Exception{
                    implement_sudoku_solve();
                    return "";
                }
            };
            swingworker.execute();                 
        }
        
        //EXECUTION OF RESET BUTTON  
        if( e.getSource() == b2 )
        {
            reset_sudoku();
        }
        
        //EXECUTION OF FUNCTION TO TAKE CSV FILE AS INPUT
        if(e.getSource() == b3){
           
            //CREATING NEW JFILECHOOSER OBJECT 
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnValue = jfc.showOpenDialog(null);
            
            if (returnValue == JFileChooser.APPROVE_OPTION){
                    File file = new File(jfc.getSelectedFile().getAbsolutePath());
                try {
                    
                    //READING THE FILE
                    FileReader rdr=new FileReader(file);
                    String row="";
                    //CREATING BUFFERREADER OBJECT
                    BufferedReader bfr=new BufferedReader(rdr);
                    //CREATING TEMPORARY MATRIX
                    String[][] arr=new String[9][9];
                    int index=0;
                    //SPLITTING CSV FILE AND STORING ELEMENTS IN ARRAY
                    while((row=bfr.readLine())!=null){
                        arr[index]=row.split(",");
                        index++;
                         
                    }
                    
                    //READING FROM THE ARRAY WHICH IS MADE FROM THE CSV FILE
                    for(int i=0;i<arr.length;i++){
                        for(int j=0;j<arr.length;j++){
                            
                            //SETTING THE NUMBER ON THE ARRAY TO THE SUDOKU GRID MATRIX ARRAY 
                            int n = Integer.parseInt(arr[i][j]);
                            grid[i][j]=n;  
                            
                            //DISPLAYING IT ON THE SUDOKU GUI BOARD
                            if(n==0){
                                tf_sudoku[i][j].setText("");   
                            }
                            else{
                                (tf_sudoku[i][j]).setText(Integer.toString(n));
                            }
                            
                        }
                    }
                    
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
            }
            
            
        }
        
    }

         
    //EXECUTION OF MAIN
    public static void main(String[] args) throws IOException{
        long start = System.currentTimeMillis();
        new Sudoku();
        
        
    }
    
           
    //EXECUTION OF SOLVE FUNCTION THAT IS CALLED IN THE SOLVE BUTTON
    private static void implement_sudoku_solve(){
        
        //SUDOKU BOARD IS CREATED FROM THE INPUT ON THE GUI BOARD
        create_sudoku();
        
        //CHECK IF THE INPUTS ON THE GUI BOARD IS CORRECT OR NOT
        if(check_input_board()){
           start = System.currentTimeMillis();
            
            //IF SUDOKU IS SOLVED SUCCESSFULLY
            if(solve_sudoku()){ 
                end = System.currentTimeMillis();
                flag = true ;
                
                //DISPLAY THE SUCCESS MESSAGE
                JOptionPane.showMessageDialog(null, "Sudoku Successfully Solved! \n Number of iterations used: "+iter+" \n Total Solving time: "+(end-start)+"mS" );
            }
            
            else{
                //OTHERWISE DISPLAY NO SOLUTION
                JOptionPane.showMessageDialog(f,"No solution");
            }
        }
        
        //IF INPUT IS NOT CORRECT THEN DISPLAY THIS INVALID INPUT MESSAGE
        else{
            JOptionPane.showMessageDialog(f,"INVALID INPUTS!");
        }
        
    }
    
    //FUNCTION TO MAKE THE SUDOOKU GRID ARRAY WITH VALUES FROM JTEXTFIELD
    private static void create_sudoku(){
        for(int i=0 ; i<9; i++ ){
            for(int j=0 ; j<9; j++ ){
                
                //FILLING THE SUDOKU 2D ARRAY
                if( ( (tf_sudoku[i][j]).getText() ).equals("") )
                    
                    grid[i][j] = 0 ;
                else
                    grid[i][j] = Integer.parseInt((tf_sudoku[i][j]).getText());
            }
        }
    }
    
    
    //CONSTRAINT SATISFACTION ALGORITHM
    //METHOD TO CHECK IF THE DIGIT TO BE FILLED IN CELL IS ALREADY PRESENT IN A ROW/COLUMN/GRID
    private static boolean check_digit(int r, int c, int n){
        
        //CHECKING ROW CLASH
         for(int col=0; col<9; col++){
            if( col != c && grid[r][col] == n ){
                return true;
            }
        }
        
        //CHECKING COLUMN CLASH
        for(int row=0; row<9; row++){ 
            if( row != r && grid[row][c] == n ){
                return true;
            }
        }
        
        //CHECKING GRID CLASH
       int grid_row_start = r - r%3;
        int grid_col_start = c - c%3;
        
        for(int p=0 ; p<3; p++){
            for(int q=0 ; q<3; q++){
                if( grid_row_start+p != r && grid_col_start+q != c && grid[grid_row_start+p][grid_col_start+q] == n ){
                    return true;
                }
            }
        }
        
        //IF NO CLASHES FOUND THEN RETURNING FALSE 
        return false;
            
    }
    
    //MAIN SUDOKU SOLVING ALGORITHM
    private static boolean solve_sudoku()
    {
        
        boolean flag1 = false;
        int row,col=0;
        
        //FINDING UNASSIGNED LOCATION ON SUDOKU BOARD
        
        for( row = 0 ; row <9; row++ ){
            for( col = 0 ; col <9; col++ ){
                if( grid[row][col] == 0 ){
                    //IF AN EMPTY PLACE IS FOUND ON THE BOARD, THEN BREAK OUT OF THE LOOP
                    flag1 = true;
                    break;
                }
            }
            
            //IF AN EMPTY PLACE IS FOUND ON THE BOARD, THEN BREAK OUT OF THE LOOP
            if( flag1 == true )
                break;
        }
        
        
        //IF NO EMPTY PLACE IS FOUND, THEN RETURN TRUE AND SUDOKU SOLUTION IS COMPLETED
        if( flag1 == false ){    
            return true ;
            
        }    
            
        
        //CHECKING ALL THE NUMBERS FROM 1-9 TO B FILLED IN THE EMPTY SPACE 
        for(int n = 1 ; n <= 9 ; n++ ){
            
            //CHECKING IF THE NUMBER TO BE FILLED CAN BE PLACED IN THE EMPTY POSITION WITHOUT ANY CLASHES (ROW, COLUMN OR GRID)
            //COSTRAINT SATISFACTION ALGORITHM
            if( !check_digit(row,col,n) ){
                
                //SETTING THE EMPTY VALUE TO THE CURRENT NUMBER BEING USED IN THE ITERATION
                grid[row][col] = n ;
                
                
                //SETTING THIS NUMBER ON THE SUDOKU BOARD IN GUI
                (tf_sudoku[row][col]).setText(Integer.toString(n));
                
                try
                {
                    //TO DISPLAY THE BACKTRACKING ON SUDOKU BOARD IN GUI
                    Thread.sleep(10);
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(f,e);
                }
                
                //RETURN TRUE IF SUCCESSFULL 
                if( solve_sudoku() ){
                    
                    return true;
                }
                
                //IF FAILING, THEN TRYING AGAIN
                else
                    grid[row][col] = 0;
                    iter++;
                    
            }
        }
        
        //PERFORMING BACKTRACKING
        return false ;
    }
    
    //FUNCTION TO RESET THE SUDOKU GRID
    private static void reset_sudoku(){
       
        for(int i=0 ; i<9; i++ ){
            for(int j=0; j<9; j++){

                //RESETTING THE JTEXTFIELD TO NULL AGAIN
                tf_sudoku[i][j].setText(""); 
            } 

        }
              
    }
    
    
    //VALIDATING AND CHECKING THE INPUT TO SUDOKU BOARD
    private static boolean check_input_board(){
        for(int i=0 ; i<9; i++ ){
            for(int j=0 ; j<9; j++ ){
                
                //CHECKING IF NUMBERS INPUTTED ARE LESS THAN 9 AND GREATER THAN 1
                if( grid[i][j]<0 || grid[i][j]>9){
                    return false ;
                }
                
                //CHECKING IF THE GRID IS EMPTY AND THE DIGIT FOR THIS ITERATON IS NOT CLASHING WITH ROW, COLUMN OR GRID
                if( (grid[i][j]!=0) && (check_digit(i,j,grid[i][j])) ){
                    return false ;
                }
            }
        }
        return true ;
    }
             
}
