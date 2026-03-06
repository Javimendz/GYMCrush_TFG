<p align="center">
  <a href="http://nestjs.com/" target="blank"><img src="https://nestjs.com/img/logo-small.svg" width="120" alt="Nest Logo" /></a>
</p>


# ```GYMCrush``` - TFG - Backend de gestión de un gimnasio

Pasos que he realizado para el proyecto.
----------------------------------------

```1.``` Crear un ```Dockerfile``` con su configuración  
```2.``` Crear un ```docker-compose.yaml``` con su configuración  
```3.``` Añadir dependencia de ```postgresql``` en el ```pom.xml```  
```4.``` Añadir dependencia de ```spring-boot-starter-web``` en el ```pom.xml```  
```5.``` Configurar la conexión a la base de datos en el ```application.properties```  
```5.``` Añadir dependencia de ``` MapStruct ``` en el ```pom.xml```
```6.``` Generación del Esquema desde el Código". Usar entidades para crear la BBDD.  
```7.``` Crear arquitectura de carpetas con un patrón de diseño ```MVC```.
```8.``` 

Mvnrepository.com - MapStruct
Nota : MapStruct es una biblioteca de mapeo de objetos que simplifica la copia de datos entre objetos de diferentes tipos. Lo utilizamos para mapear entre objetos de entidad y objetos de transferencia de datos (DTO).

# /Comandos

 ```0.``` Exportar extensiones VS Code ``` code --list-extensions > extensiones.txt```, reinstalar las extensiones ```cat extensiones.txt | xargs -L 1 code --install-extension```

 ```1.``` Iniciar todo el proyecto primera vez ```docker compose up --build ```. Descarga imagenes, etc.  
 ```2.``` Iniciar todo rapido ```docker compose up -d ```
 ```3.``` Detener todo el proyecto ```docker compose down ```
