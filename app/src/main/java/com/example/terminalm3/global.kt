package com.example.terminalm3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.terminalm3.screen.lazy.Console
import kotlinx.coroutines.flow.MutableStateFlow

// ╔═══════════════════════╗
// ║ Глобальные переменные ║
// ╚═══════════════════════╝


// ╔═══════╗
// ║ ФЛАГИ ║
// ╚═══════╝
//🟠🟡🟢🟣🟤🟦🟧🟨🟩🟪🟫

var isInitialized =
        false                                               //🟦Флаг того что произошла инициализация

var isCheckUseCRLF by mutableStateOf(false) //🟦Показывать в конце строки символ CR LF

val isCheckedUseLineVisible by mutableStateOf(false)  //🟦Показывать номер строки
var telnetSlegenie = MutableStateFlow(true)           //🟦Слежение за последней строкой

/* ----------------------------------------------------------------- */
/**
 * ## Размер текста в консоли
 */ //var console_text by mutableIntStateOf( 12 )


/////////////////////////////////////////////////////////
/**
 *
 * ##
 */
var telnetWarning = MutableLiveData(false) //Для отображения значка внимание

val warning = MutableStateFlow(false)






//╔═════╗
//║ NET ║
//╚═════╝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┑
var ipBroadcast = "0.0.0.0"            //│

                                       //│
//IP адрес ESP                         //│
var ipESP = "0.0.0.0"                  //│

//Признак тог что ip адрес есп найден  //│
var isESPmDNSFinding = false           //│
//━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┙

//╔═════════╗
//║ CONSOLE ║
//╚═════════╝
val console = Console()








