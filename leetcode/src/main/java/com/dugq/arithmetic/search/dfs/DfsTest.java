package com.dugq.arithmetic.search.dfs;

import com.dugq.arithmetic.util.MyArrayUtils;
import org.junit.jupiter.api.Test;

public class DfsTest {

    @Test
    public void test(){
        char[][] chars = MyArrayUtils.readCharArray("[[\"O\",\"O\",\"O\",\"O\",\"X\",\"X\"],[\"O\",\"O\",\"O\",\"O\",\"O\",\"O\"],[\"O\",\"X\",\"O\",\"X\",\"O\",\"O\"],[\"O\",\"X\",\"O\",\"O\",\"X\",\"O\"],[\"O\",\"X\",\"O\",\"X\",\"O\",\"O\"],[\"O\",\"X\",\"O\",\"O\",\"O\",\"O\"]]");
        solve(chars);
}

    public void solve(char[][] board) {
        for(int i = 1; i<board.length-1; i++){
            for(int j = 1;j<board[0].length-1;j++){
                if(board[i][j]=='O' && isArea(board,i,j)){
                    System.out.println(i+"-"+j);
                    change(board,i,j);
                }
            }
        }
        for(int i = 1; i<board.length-1; i++){
            for(int j = 1;j<board[0].length-1;j++){
                if(board[i][j]=='o'){
                    board[i][j] = 'O';
                }
            }
        }
    }

    void change(char[][] board,int x ,int y){
        if(board[x][y]=='o'){
            board[x][y] = 'X';
            change(board,x-1,y);
            change(board,x+1,y);
            change(board,x,y-1);
            change(board,x,y+1);
        }

    }

    Boolean isArea(char[][] board,int x ,int y){
        if(board[x][y]=='X' || board[x][y]=='o'){
            return true;
        }
        if(x==0 || y == 0 || x == board.length-1 || y == board[0].length-1){
            return false;
        }
        boolean result = true;
        board[x][y] = 'o';
        if(!isArea(board,x-1,y)){
            result = false;
        }
        if(!isArea(board,x+1,y)){
            result = false;
        }
        if(!isArea(board,x,y-1)){
            result = false;
        }
        if(!isArea(board,x,y+1)){
            result = false;
        }
        return result;
    }

}
