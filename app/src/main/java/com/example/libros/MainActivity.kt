package com.example.libros

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*

import androidx.navigation.Navigation
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.libros.ui.theme.LibrosTheme
import com.example.libros.vistaJetpack.Repository
import com.example.libros.vistaJetpack.ResponseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.nio.file.Files.delete
import kotlin.coroutines.CoroutineContext
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Call

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibrosTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ScreenLibros()
                }
            }
        }
    }
}

@Composable
fun ScreenLibros(){
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { screenTopBar(scope = scope, scaffoldState = scaffoldState) },
        drawerContent = {
            viewD(scope = scope, scaffoldState = scaffoldState, navController = navController)
        }
    ) {
        Navigation(navController = navController)
    }
}


@Composable
fun screenTopBar(scope: CoroutineScope, scaffoldState: ScaffoldState){
    TopAppBar(
        title = { Text(text = "Visualizar", fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        backgroundColor = Color(0xFFEACFBE),
        contentColor = Color(0xFFEACFBE)
    )
}

@Composable
fun viewD(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController){

    val items = listOf(
        NavItem.Insertar,
        NavItem.Borrar,
        NavItem.Visualizar,

        )

    Column(
        modifier = Modifier.background(color = Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.libreria),
                contentDescription = "",
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            )

        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { items ->
            viewDitem(item = items, selected = currentRoute == items.route, onItemClick = {

                navController.navigate(items.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }

                scope.launch {
                    scaffoldState.drawerState.close()
                }

            })
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Biblioteca",
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )

    }
}

@Composable
fun viewDitem(item: NavItem, selected: Boolean, onItemClick: (NavItem) -> Unit) {
    val background = if (selected) R.color.rosa else android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)
    ) {

        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            colorFilter = ColorFilter.tint(Color.Black),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = Color.Black
        )

    }

}

@Composable
fun Insertar() {

    insertar()
}

@Composable
fun Borrar() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        delete()
    }
}

@Composable
fun insertar() {
    val context= LocalContext.current
    var textFieldValueTitulo by rememberSaveable { mutableStateOf("") }
    var textFieldValueAutor by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize() ,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        Spacer(modifier = Modifier.padding(40.dp))
        Text(
            text = "GUARDA UN LIBRO",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )


        TextField(
            value = textFieldValueTitulo,
            onValueChange = { nuevo ->
                textFieldValueTitulo = nuevo
            },
            label = {
                Text(text = "Introduce el título del libro")
            },
            modifier = Modifier
                .padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )


        TextField(
            value = textFieldValueAutor,
            onValueChange = { nuevo ->
                textFieldValueAutor = nuevo
            },
            label = {
                Text(text = "Introduce el nombre del autor/a")
            },
            modifier = Modifier
                .padding( 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )


        Spacer(Modifier.height(20.dp) )


        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .size(width = 100.dp, height = 50.dp)
            ,


            onClick = {
                if(textFieldValueTitulo.isEmpty()||textFieldValueAutor.isEmpty()){
                    Toast.makeText(context,"Dede rellenar los campos solicitados", Toast.LENGTH_SHORT).show()

                }
                else {
                    insertar(textFieldValueTitulo, textFieldValueAutor)
                    Toast.makeText(context,"Se ha guardado", Toast.LENGTH_LONG).show()
                    textFieldValueTitulo = ""
                    textFieldValueAutor = ""

                }
            }
        ){
            Text(text = "Insert"
            )
        }


    }

}

fun insertar(titulo:String,autor:String){

    val url = "http://iesayala.ddns.net/abigail/addLibro.php/?titulo=$titulo&autor=$autor"

    leerUrl(url)

}

@Composable
fun delete() {

    var textFieldValueTitulo by rememberSaveable { mutableStateOf("") }
    val context= LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize() ,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        Text(
            text = "ELIMINAR LIBRO",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )

        TextField(
            value = textFieldValueTitulo,
            onValueChange = { nuevo ->
                textFieldValueTitulo = nuevo
            },
            label = {
                Text(text = "Introduce el título")
            },
            modifier = Modifier
                .padding( 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Left)
        )

        Spacer(Modifier.height(20.dp) )


        Button(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .size(width = 100.dp, height = 50.dp)
            ,


            onClick = {
                if(textFieldValueTitulo.isEmpty()){
                    Toast.makeText(context,"Rellene el campo solicitado", Toast.LENGTH_SHORT).show()

                }
                else {
                    borrar(textFieldValueTitulo)
                    Toast.makeText(context,"Se ha borrado el libro", Toast.LENGTH_SHORT).show()
                    textFieldValueTitulo = ""

                }

            }
        ){
            Text(text = "Borrar"
            )
        }


    }

}

fun borrar(titulo:String) {

    val url = "http://iesayala.ddns.net/abigail/eliminarLibro.php/?titulo=$titulo"

    leerUrl(url)

}

@Composable
fun cargarJson(): Repository {

    var users by rememberSaveable { mutableStateOf(Repository()) }
    val user = ResponseRepository.responseInterface.responseLibros()

    user.enqueue(object : Callback<Repository> {
        override fun onResponse(
            call: Call<Repository>,
            response: Response<Repository>
        ){
            val repository : Repository?= response.body()
            if(repository != null){
                users = repository
            }
        }
        override fun onFailure(call: Call<Repository>, t: Throwable)
        {
        }
    })

    return users
}

fun leerUrl(urlString:String){
    GlobalScope.launch(Dispatchers.IO)   {
        val response = try {
            URL(urlString)
                .openStream()
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            "Error with ${e.message}."
            Log.d("io", e.message.toString())
        } catch (e: Exception) {
            "Error with ${e.message}."
            Log.d("io", e.message.toString())
        }
    }

    return
}

@Composable
fun VisualizarItems() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Llamada()

    }

}

@Composable
fun Llamada() {
    var lista= cargarJson()
    Row(){
        Column(modifier = Modifier.weight(4f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text="Título",
                color = Color(0xFFEACFBE),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(20.dp),

                )
        }
        Column(modifier = Modifier.weight(4f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text="Autor",
                color = Color(0xFFEACFBE),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(20.dp),

                )
        }
    }
    LazyColumn()

    {
        items(lista) { usu ->
            Box(
                Modifier
                    .background(Color.White)
                    .width(370.dp)){
                Row(){
                    Column(modifier = Modifier.weight(4f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text=usu.titulo,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(25.dp),

                            )
                    }
                    Column(modifier = Modifier.weight(3f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text=usu.autor,
                            color = Color.Black,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(25.dp),
                        )
                    }

                }


            }
            Spacer(
                modifier = Modifier
                    .height(5.dp)
            )


        }
    }


}

@Composable
fun Navigation(navController: NavHostController) {

    NavHost(navController, startDestination = NavItem.Insertar.route) {

        composable(NavItem.Insertar.route) {
            Insertar()
        }

        composable(NavItem.Borrar.route) {
            Borrar()
        }

        composable(NavItem.Visualizar.route) {
            VisualizarItems()
        }


    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LibrosTheme {
        ScreenLibros()
    }

}



