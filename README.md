# Juego de Blackjack


Este es un proyecto de implementación de un juego de Blackjack en un entorno cliente-servidor. El juego sigue las reglas básicas del Blackjack y presenta una lógica adicional para gestionar las desconexiones de los clientes durante una partida.

##Integrantes
- Jhon Jairo Castro Mancipe
- Juan Sebastian Zarate Ortíz
- Pedro Edudardo Cruz Lopez
- 
## Reglas básicas

- El juego solo debe comenzar cuando se alcance el número total de jugadores para la partida (3 ó 4, dependiendo del número de miembros del grupo).
- Una vez que la partida esté en curso, no podrán conectarse más jugadores.
- Cuando la partida termina, los jugadores pueden decidir si quieren jugar otra ronda, esperando a que empiece de nuevo, y todas las jugadas deben reiniciarse.
- Una vez concluida la partida, el servidor debe aceptar nuevas conexiones en caso de que uno o más jugadores decidan abandonar la partida.
- Durante la partida, las cartas deben enviarse como objetos, no como cadenas, valores numéricos o similares. Debe ser un objeto.
- Todos los datos enviados entre el cliente y el servidor deben estar en forma de objetos, no de cadenas, enteros, booleanos, etc.
- El resultado del juego debe enviarse en forma de archivo; no puede ser una cadena, un entero, un objeto, etc.

## Cartas

- Las cartas se reparten de una baraja estándar.
- Cada jugador recibe dos cartas al principio.
- Muestra en pantalla cada carta que recibe el jugador.

## Valores de las cartas

- Las cartas numéricas tienen su valor nominal.
- Las cartas de cara (J, Q, K) valen 10.
- El As puede valer 1 u 11, dependiendo de la mano.

## Gestión de desconexiones

Implementa una lógica para gestionar las desconexiones de los clientes durante una partida y garantizar que la partida continúe sin problemas.

## Instalación y configuración del proyecto

1. Clona este repositorio en tu máquina local.
2. Asegúrate de tener instalado Python 3 en tu sistema.
3. Navega hasta el directorio del proyecto y crea un entorno virtual con el comando `python3 -m venv venv`.
4. Activa el entorno virtual con el comando `source venv/bin/activate` (Linux/Mac) o `venv\Scripts\activate` (Windows).
5. Instala las dependencias del proyecto con el comando `pip install -r requirements.txt`.
6. Configura la dirección IP y el puerto del servidor en el archivo `server.py`.
7. Ejecuta el servidor con el comando `python server.py` y asegúrate de que esté en funcionamiento.
8. Configura la dirección IP y el puerto del servidor en el archivo `client.py`.
9. Ejecuta el cliente con el comando `python client.py` y únete a la partida.
