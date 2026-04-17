package com.example.terminalm3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.terminalm3.console.Console

// ╔═══════════════════════╗
// ║ Глобальные переменные ║
// ╚═══════════════════════╝


// ╔═══════╗
// ║ ФЛАГИ ║
// ╚═══════╝
//🟠🟡🟢🟣🟤🟦🟧🟨🟩🟪🟫

object Global {


    /**
     * 🟦 Флаг того что произошла инициализация
     */
    var isInitialized = false

    var isCheckUseCRLF by mutableStateOf(false) //🟦Показывать в конце строки символ CR LF

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

}

/* ----------------------------------------------------------------- */
/**
 * ## Размер текста в консоли
 */ //var console_text by mutableIntStateOf( 12 )


/////////////////////////////////////////////////////////
//╔═════════╗
//║ CONSOLE ║
//╚═════════╝
val console = Console()








