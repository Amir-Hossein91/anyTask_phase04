package com.example.phase_04.utility;

import java.util.List;

public class Printer {

    public void printMenu(String[] menu){
        String titleBorder = "====================================";
        String endOfMenu = "------------------------------------";
        System.out.println();
        System.out.println(titleBorder);
        int shift = (titleBorder.length() - menu[0].length())/2;
        for (int i =1; i<= shift; i++)
            System.out.print(" ");
        System.out.println(menu[0]);
        System.out.println(titleBorder);
        for(int i=1; i<menu.length; i++)
            System.out.println(i + ") " + menu[i]);
        System.out.println(endOfMenu);
        askToSelect();
    }

    public void askToSelect(){
        System.out.print("Select--> ");
    }

    public void getInput(String msg){
        System.out.print("*) " + msg + " --> ");
    }

    public void printMessage(String msg){
        System.out.println();
        System.out.println("***" + msg + "***");
    }

    public void printError(String msg){
        if(msg == null){
            System.out.println();
            System.out.println(">>>> Wrong entry! <<<<");
            System.out.println();
        }
        else{
            System.out.println();
            System.out.println(">>>> " + msg + " <<<<");
            System.out.println();
        }
    }

    public void printResult(String title, List<String> result){
        String titleBorder = "************************************";
        String endOfResult = "************************************";
        System.out.println();
        System.out.println(titleBorder);
        int shift = (titleBorder.length() - title.length())/2;
        for (int i =1; i<= shift; i++)
            System.out.print(" ");
        System.out.println(title);
        System.out.println(titleBorder);
        result.forEach(System.out::println);
        System.out.println(endOfResult);
    }

    public void printResult(String title, String result){
        String titleBorder = "************************************";
        String endOfResult = "************************************";
        System.out.println();
        System.out.println(titleBorder);
        int shift = (titleBorder.length() - title.length())/2;
        for (int i =1; i<= shift; i++)
            System.out.print(" ");
        System.out.println(title);
        System.out.println(titleBorder);
        System.out.println(result);
        System.out.println(endOfResult);
    }

    public void printListWithSelect(List<String> list){
        System.out.println("------------------------------------");
        for(int i = 0; i<list.size(); i++){
            System.out.println(i+1 + ") " + list.get(i));
        }
        System.out.println("------------------------------------");
        System.out.print("Select -> ");
    }

    public void printListWithoutSelect(List<String> list){
        System.out.println("------------------------------------");
        for(int i = 0; i<list.size(); i++){
            System.out.println(i+1 + ") " + list.get(i));
        }
        System.out.println("------------------------------------");
    }
}
