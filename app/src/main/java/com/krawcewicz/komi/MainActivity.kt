package com.krawcewicz.komi

import android.app.PendingIntent.getActivity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.util.Random
import android.os.Handler
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner1 : Spinner = findViewById(R.id.spinner1)
        val spinner2 : Spinner = findViewById(R.id.spinner2)
        val editDistance : EditText = findViewById(R.id.editTxtDistance)
        val change : Button = findViewById(R.id.btnChange)
        val route : Button = findViewById(R.id.btnRoute)
        val textValue : TextView = findViewById(R.id.textRouteValue)
        val MyLayoutView : View = findViewById(R.id.MyLayout)
        //Macierz odległości między miastami wypełniona 0
        val cities = List<MutableList<Int>>(8){
            MutableList<Int>(8){0}
        }
        //Pętla wypełniająca macierz z odległościami, losowymi elementami
        //Pętla w postaci trójkąta, która powoduje że między tymi samymi punktami odległość pozostaje 0
        for(i in 0..7){
            for(j in i+1..7){
                Random().nextInt(1089).let{
                    cities[i][j]=it
                    cities[j][i]=it
                }
            }
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.cities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner1.adapter = adapter
            spinner2.adapter = adapter
        }

        //Funkcja sprawdzająca wybrane elementy ze spinnera i wypisujące odglełość z macierzy między tymi elementami do pola tekstowego
        fun checkDistance(){
            editDistance.setText(cities[spinner1.selectedItemId.toInt()][spinner2.selectedItemId.toInt()].toString())
        }

        //W przypadku zmiany zaznaczonego elementu w spinnerze wywołuje funkcje sprawdzającą
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                checkDistance()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //W przypadku zmiany zaznaczonego elementu w spinnerze wywołuje funkcje sprawdzającą
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Wywyołanie funkcji, która sprawdza odległość
                checkDistance()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        change.setOnClickListener{
            if(!editDistance.text.isNullOrEmpty()){
                if(spinner1.selectedItemId.toInt() == spinner2.selectedItemId.toInt()){
                }else{
                    if(editDistance.text.toString().toInt()==0){
                    }else{
                        cities[spinner1.selectedItemId.toInt()][spinner2.selectedItemId.toInt()] = editDistance.getText().toString().toInt()
                        cities[spinner2.selectedItemId.toInt()][spinner1.selectedItemId.toInt()] = editDistance.getText().toString().toInt()
                    }
                }
            }
        }

        fun calcRoute(){
            // Algorytm działa na wyżej stworzonej macierzy, polega na heurystyce znajdywaniu najbliższego sąsiada
            val size = 8
            // Tablica przechowująca odwiedzone miasta
            val visited = BooleanArray(size)

            // Tablica przechowująca najkrótszą ścieżkę
            val path = IntArray(size)

            // Miasto startowe
            var currentCity = 0

            // Startowe miasto zostaje dodane do tablicy odwiedzonych miast
            visited[currentCity] = true

            for (i in 0 until size - 1) {
                // Zmienna przechowująca najkrótszy dystans, na początku jest to nieskończoność (w tym przypadku maksymalna wartość inta)
                var min = Int.MAX_VALUE

                // Zmienna przechowująca indeks miasta z najkrótszym dystansem
                var cityIndex = 0

                // Szukanie miasta z najkrótszym dystansem
                for (j in 0 until size) {
                    if (!visited[j] && cities[currentCity][j] < min){
                        min = cities[currentCity][j]
                        cityIndex = j
                    }
                }

                // Dodanie miasta do ścieżki i oznaczenie jako odwiedzone
                path[i] = cityIndex
                visited[cityIndex] = true

                // Zmiana miasta na którym pracuje algorytm
                currentCity = cityIndex
            }

            // Dodanie ostatniego miasta do ścieżki
            path[size - 1] = 0

            //Wypisanie najkrótszej ścieżki
            for (i in 0 until size) {
                textValue.append("${path[i]} -> ")
            }

            // Wypisanie całkowitej odległości od punkt pierwszego do ostatniego
            var cost = 0
            for (i in 0 until size - 1) {
                cost += cities[path[i]][path[i + 1]]
            }
            textValue.append("\nCałkowita odległość: $cost")
        }
    }
}