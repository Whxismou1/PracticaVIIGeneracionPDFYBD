# SIS 2: Práctica 7 Sistema generador de recibos PDF & BBDD

## Descripción

Esta es la cuarta práctica de Java y la séptima práctica de la asignatura Sistemas de la Información 2 de tercer curso. En esta práctica, se amplía la funcionalidad de la [Práctica 6](https://github.com/Whxismou1/SIS2-PracticaVI-GeneradorRecibos) para generar recibos en PDF basados en los datos validados y corregidos, además de los recibos XML.
El programa:
1. Realiza las mismas comprobaciones y acciones anteriores.
2. Calcula las ordenanzas y recibos de los contribuyentes y los guarda en un archivo [recibos.xml](src/resources/recibos.xml).
3. Genera los recibos en formato PDF para cada contribuyente en función del trimestre y se guardan dentro de la carpeta [recibos](src/resources/recibos).
4. Finalmente introduce los cambios en la base de datos

## Requisitos

- Java 8 o superior
- Apache POI (para manipular archivos Excel)
- Una librería para generar archivos XML (por ejemplo, JAXB)
- Una librería para generar archivos PDF (por ejemplo, iText)

## Instalación

1. Clona este repositorio:
   ```sh
   git clone https://github.com/Whxismou1/SIS2-PracticaVII-NIFAndCCCSystem.git
