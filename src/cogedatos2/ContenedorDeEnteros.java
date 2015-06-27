package cogedatos2;

import java.util.*;

public class ContenedorDeEnteros {

    int minimoClaves;
    protected int raiz;                                                                             //Direccion de la raiz en el fichero
    protected int numElem;                                                                  //Numero de datos en el multirrama
    protected int Orden;                                                                    //Orden del arbol B
    protected int tamañoDatos;                                                             //Tamaño de cada dato almacenado
    protected String nombreFichero;                                                 //Fichero donde se almacenan los nodos
    protected FicheroAyuda fichero = new FicheroAyuda();    //Objeto para conversiones a tipo almacenado

    protected class Nodo {

        private int[] clavei;                                   //Claves [1..numEle]
        private int[] enlacei;                                  //Enlaces [0..numEle]
        private int numElei;                                    //Numero de datos en la pagina
        private int direccióni;                                        //Direccion de la pagina en el fichero

        Nodo() {
            direccióni = FicheroAyuda.dirNula;
            numElei = 0;
            //El tamaño depende de "Orden" del objeto donde se crea
            clavei = new int[Orden];
            enlacei = new int[Orden + 1];
        }

        int tamaño() {
            int tam = 2 * Conversor.INTBYTES;       //Cantidad base
            tam += (Orden - 1) * tamañoDatos;     //Los datos
            tam += Orden * Conversor.INTBYTES;      //Los enlaces
            return tam;
        }

        int clave(int i) throws Exception {                                    //Devuelve la clave
            if (i < 1 || i > numElei) {
                throw new Exception("Error interno en el multirrama");
            }
            return clavei[i - 1];
        }

        void clave(int i, int d) throws Exception {                    //Establece la clave
            if (i < 1 || i > numElei) {
                throw new Exception("Error interno en el multirrama");
            }
            clavei[i - 1] = d;
        }

        int enlace(int i) throws Exception {                                   //Devuelve el enlace
            if (i < 0 || i > numElei) {
                throw new Exception("Error interno en el multirrama");
            }
            return enlacei[i];
        }

        void enlace(int i, int d) throws Exception {                   //Establece la clave
            if (i < 0 || i > numElei) {
                throw new Exception("Error interno en el multirrama");
            }
            enlacei[i] = d;
        }

        int dirección() {                                              //Devuelve la dirección donde se almacena
            return direccióni;
        }

        void dirección(int d) {                                        //Establece el número de datos almacenados
            direccióni = d;
        }

        int cardinal() {                                                //Develve el número de datos almacenados
            return numElei;
        }

        void cardinal(int n) {                                  //Establece el número de datos almacenados
            numElei = n;
        }

        /**
         * @return el nodo en forma de byte[]
         */
        byte[] abyte() {
            int tam = tamaño();
            byte[] res = new byte[tam];
            int pos = 0;
            pos = Conversor.añade(res, Conversor.aByte(direccióni), pos);
            pos = Conversor.añade(res, Conversor.aByte(numElei), pos);
            for (int i = 0; i < numElei; i++) {
                pos = Conversor.añade(res, Conversor.aByte(clavei[i]), pos);
            }
            for (int i = 0; i <= numElei; i++) {
                pos = Conversor.añade(res, Conversor.aByte(enlacei[i]), pos);
            }
            return res;
        }

        /**
         * Inicializa el nodo partiendo de un byte[] previamente generado con
         * aByte
         */
        void deByte(byte[] datos) {
            int leb = Conversor.INTBYTES;           //Longitud de los enteros en bytes
            dirección(Conversor.aInt(Conversor.toma(datos, 0, leb)));
            numElei = Conversor.aInt(Conversor.toma(datos, 0, leb));
            int baseClaves = leb * 2;
            int baseEnlaces = baseClaves + (numElei) * tamañoDatos;
            for (int i = 0; i < numElei; i++) {
                clavei[i] = Conversor.aInt(Conversor.toma(datos, baseClaves + i * tamañoDatos, tamañoDatos));
            }
            for (int i = 0; i <= numElei; i++) {
                byte[] dato = Conversor.toma(datos, baseEnlaces + i * leb, leb);
                enlacei[i] = Conversor.aInt(dato);
            }
        }

        /**
         * @param e -> Dato a buscar
         * @return La posición de la clave menor que es mayor o igual a e
         * @throws Exception
         */
        public int buscarPosic(int e) throws Exception {
            int pos, prim, ulti, med;
            prim = 1;
            ulti = cardinal();

            while (prim <= ulti) {
                med = (prim + ulti) / 2;
                if (e == (clave(med))) {
                    pos = med;
                    return pos;
                }

                if (e < (clave(med))) {
                    ulti = med - 1;
                } else {
                    prim = med + 1;
                }
            }
            pos = prim - 1;
            return pos;
        }

        /**
         * @param e -> Dato a buscar
         * @return Verdadero si el dato pasado está o falso si no está en el
         * nodo
         * @throws Exception
         */
        public boolean buscar(int e) throws Exception {
            int pos, prim, ulti, med;
            prim = 1;
            ulti = cardinal();

            while (prim <= ulti) {
                med = (prim + ulti) / 2;
                if (e == (clave(med))) {
                    pos = med;
                    return true;
                }

                if (e < (clave(med))) {
                    ulti = med - 1;
                } else {
                    prim = med + 1;
                }
            }
            return false;
        }

        /**
         * @param e
         * @param dir
         * @param pos
         */
        void insertar(int e, int dir, int pos) throws Exception {
            numElei++;
            for (int i = numElei - 1; i >= pos; i--) {
                clave(i + 1, clave(i));
                enlace(i + 1, enlace(i));
            }

            clave(pos, e);
            enlace(pos, dir);
        }

        /**
         * @param pos
         */
        void extraer(int pos) throws Exception {
            for (int i = pos; i < numElei; i++) {
                clave(i, clave(i + 1));
                enlace(i, enlace(i + 1));
            }
            numElei--;
        }
    }

    class InfoPila {

        public Nodo nodo;
        public int pos;

        public InfoPila() {
        }

        ;
               
                public InfoPila(Nodo n, int p) {
            nodo = n;
            pos = p;
        }
    }

    Nodo leer(int dir) throws Exception {
        Nodo n = new Nodo();
        n.deByte(fichero.leer(dir));
        if (n.dirección() != dir) {
            throw new Exception("Error al leer un nodo del arbol");
        }
        return n;
    }

    /**
     * @param n
     */
    void escribir(Nodo n) {
        fichero.escribir(n.abyte(), n.dirección());
    }

    /**
     * @param n -> Elemento a buscar
     * @return Verdadero o falso
     */
    public boolean buscar(int n) throws Exception {
        return buscar(n, new Stack());
    }

    /**
     * @param e -> Dato a buscar en forma de byte[]
     * @param pila -> Donde se almacena el camino
     * @return Verdadero o falso
     * @throws Exception
     */
    boolean buscar(int e, Stack pila) throws Exception {
        int dirNodo, pos;
        Nodo nodo = new Nodo();
        dirNodo = raiz;
        pila.clear();

        while (dirNodo != FicheroAyuda.dirNula) {
            nodo = leer(dirNodo);
            pos = nodo.buscarPosic(e);
            pila.add(new InfoPila(nodo, pos));
            if (nodo.buscar(e)) {
                return true;
            }
            dirNodo = nodo.enlace(pos);
        }
        return false;
    }

    /**
     * @param e
     */
    public boolean encuentra(int e) throws Exception {
        return buscar(e, new Stack());
    }

    private class ParejaInsertar {

        public int clave;
        public int dirección;
    }

    ParejaInsertar partición_1_2(Nodo nodo) throws Exception {
        ParejaInsertar pa = new ParejaInsertar();
        Nodo nuevoNodo = new Nodo();
        int ncnuevo = Orden / 2;
        int ncnodo = Orden - ncnuevo - 1;
        int dirNuevo = fichero.tomarPagina();
        nuevoNodo.dirección(dirNuevo);
        nuevoNodo.cardinal(ncnuevo);
        nuevoNodo.enlace(0, nodo.enlace(ncnodo + 1));
        //[xxxx] [    ] -> [xx  ] [xx  ]
        for (int i = 1; i <= nuevoNodo.cardinal(); i++) {
            nuevoNodo.clave(i, nodo.clave(ncnodo + 1 + i));
            nuevoNodo.enlace(i, nodo.enlace(ncnodo + 1 + i));
        }

        pa.clave = nodo.clave(ncnodo + 1);
        pa.dirección = nuevoNodo.dirección();
        nodo.cardinal(ncnodo);
        escribir(nodo);
        escribir(nuevoNodo);
        return pa;
    }

    /**
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    void partición_2_3(Nodo padre, int posizq, Nodo izq, Nodo der) throws Exception {
        int clavesRepartir = izq.cardinal() + der.cardinal() - 1;
        Nodo reg = new Nodo();
        int ncizq = clavesRepartir / 3;
        int ncreg = (clavesRepartir + 1) / 3;
        int ncder = (clavesRepartir + 2) / 3;
        int antncder = der.cardinal();
        int antncizq = izq.cardinal();
        //Se inserta en el padre una nueva clave y la nueva dirección
        reg.dirección(fichero.tomarPagina());
        padre.insertar(izq.clave(ncizq + 1), reg.dirección(), posizq + 1);
        //Pasamos datos de izq a reg: [xxx] [   ] -> [xx ] [x  ]
        reg.cardinal(ncreg);
        reg.enlace(0, izq.enlace(ncizq + 1));
        for (int i = ncizq + 2; i <= antncizq; i++) {
            reg.clave(i - ncizq - 1, izq.clave(i));
            reg.enlace(i - ncizq - 1, izq.enlace(i));
        }

        izq.cardinal(ncizq);
        //Pasamos el dato del padre a la posición correspondiente de reg
        reg.clave(antncizq - ncizq, padre.clave(posizq + 2));
        int pos1 = antncizq - ncizq;
        reg.enlace(pos1, der.enlace(0));                //[x  ] [yyy] -> [xy ] [ yy]
        for (int i = pos1 + 1; i <= ncreg; i++) {
            reg.clave(i, der.clave(i - pos1));
            reg.enlace(i, der.enlace(i - pos1));
        }

        int ncpas = antncder - ncder;
        //Pasamos al padre el valor correspondiente y compactamos der
        padre.clave(posizq + 2, der.clave(ncpas));
        der.enlace(0, der.enlace(ncpas));               //[ yy] -> [yy ]
        for (int i = ncpas + 1; i <= antncder; i++) {
            der.clave(i - ncpas, der.clave(i));
            der.enlace(i - ncpas, der.enlace(i));
        }

        der.cardinal(ncder);
        escribir(izq);                          //[xx ] [xy ] [yy ]
        escribir(reg);
        escribir(der);
    }

    /**
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    void rotaciónizqder(Nodo padre, int posizq, Nodo izq, Nodo der) throws Exception {
        int clavesRepartir = izq.cardinal() + der.cardinal();
        int ncizq = clavesRepartir / 2;
        int ncder = clavesRepartir - ncizq;
        int ncpas = ncder - der.cardinal();
        int antncder = der.cardinal();
        //Hacemos hueco en nodo der: [yy  ] -> [ yy ]
        der.cardinal(ncder);
        for (int i = antncder; i >= 1; i--) {
            der.clave(i + ncpas, der.clave(i));
            der.enlace(i + ncpas, der.enlace(i));
        }

        der.enlace(ncpas, der.enlace(0));
        //Rellenar el nodo der: [xxxx] [ yy ] -> [xxx ] [xyy ]
        der.clave(ncpas, padre.clave(posizq + 1));
        for (int i = ncizq + 2; i <= izq.cardinal(); i++) {
            der.clave(i - (ncizq + 1), izq.clave(i));
            der.enlace(i - (ncizq + 1), izq.enlace(i));
        }
        der.enlace(0, izq.enlace(ncizq + 1));
        //Modificar el nodo padre
        padre.clave(posizq + 1, izq.clave(ncizq + 1));
        //Modificar le nodo izq
        izq.cardinal(ncizq);
        //Se escribe en el fichero los tres nodos
        escribir(padre);
        escribir(izq);
        escribir(der);
    }

    /**
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    void rotaciónderizq(Nodo padre, int posizq, Nodo izq, Nodo der) throws Exception {
        int clavesRepartir = izq.cardinal() + der.cardinal();
        int ncder = clavesRepartir / 2;
        int ncizq = clavesRepartir - ncder;
        int ncpas = der.cardinal() - ncder;
        int antncizq = izq.cardinal();
        //Pasamos la clave del padre y datos de der a izq
        izq.cardinal(ncizq);
        izq.clave(antncizq + 1, padre.clave(posizq + 1));
        izq.enlace(antncizq + 1, der.enlace(0));
        for (int i = 1; i < ncpas; i++) {                        //[xx  ] [yyyy] -> [xxy ] [ yyy]
            izq.clave(antncizq + 1 + i, der.clave(i));
            izq.enlace(antncizq + 1 + i, der.enlace(i));
        }

        //Pasamos clave al padre
        padre.clave(posizq + 1, der.clave(ncpas));
        //Quitamos hueco en der
        der.enlace(0, der.enlace(ncpas));
        for (int i = 1; i <= ncder; i++) {                       //[ yyy] -> [yyy ]
            der.clave(i, der.clave(i + ncpas));
            der.enlace(i, der.enlace(i + ncpas));
        }

        der.cardinal(ncder);
        escribir(padre);
        escribir(izq);
        escribir(der);

    }

    /**
     * @param padre
     * @param posizq
     * @param izq
     * @param der
     */
    void recombinación_2_1(Nodo padre, int posizq, Nodo izq, Nodo der) throws Exception {
        //Bajamos la clave discriminante en el padre al final del izquierdo
        int antncizq = izq.cardinal();
        izq.cardinal(izq.cardinal() + 1 + der.cardinal());
        izq.clave(antncizq + 1, padre.clave(posizq + 1));
        //Pasamos el enlace cero de der a izq ya que en encaja en el bucle
        izq.enlace(antncizq + 1, der.enlace(0));
        //Pasamos el resto de claves y enlaces
        //[xx  ] [xx  ] -> [xxxx] [    ]
        for (int i = 1; i <= der.cardinal(); i++) {
            izq.clave(antncizq + 1 + i, der.clave(i));
            izq.enlace(antncizq + 1 + i, der.enlace(i));
        }

        //Quitamos del padre la clave y el enlace a der
        padre.extraer(posizq + 1);
        escribir(izq);
        fichero.liberarPagina(der.dirección());
    }

    /**
     * @param padre
     * @param posReg
     * @param izq
     * @param reg
     * @param der
     */
    void recombinación_3_2(Nodo padre, int posReg, Nodo izq, Nodo reg, Nodo der) throws Exception {
        int aRepartir = izq.cardinal() + reg.cardinal() + der.cardinal() + 1;
        int ncder = aRepartir / 2;
        int ncizq = aRepartir - ncder;
        int antncizq = izq.cardinal();
        int antncder = der.cardinal();
        //Rellenamos el hermano izquierdo
        izq.cardinal(ncizq);
        izq.clave(antncizq + 1, padre.clave(posReg));
        izq.enlace(antncizq + 1, reg.enlace(0));
        //[xx  ] [yy  ] -> [xxy ] [ y  ]
        for (int i = antncizq + 2; i <= ncizq; i++) {
            izq.clave(i, reg.clave(i - antncizq - 1));
            izq.enlace(i, reg.enlace(i - antncizq - 1));
        }

        //Desplazamiento del hermano derecho para hacer hueco
        der.cardinal(ncder);
        int ncpas = ncder - antncder;
        for (int i = antncder; i >= 1; i--) {            //[zz  ] -> [ zz ]
            der.clave(i + ncpas, der.clave(i));
            der.enlace(i + ncpas, der.enlace(i));
        }

        der.enlace(ncpas, der.enlace(0));
        der.clave(ncpas, padre.clave(posReg + 1));
        //Rellenamos el hermano derecho
        //[ y  ] [ zz ] -> [    ] [yzz ]
        for (int i = ncpas - 1; i >= 1; i--) {
            der.clave(i, reg.clave(reg.cardinal() + i - ncpas + 1));
            der.enlace(i, reg.enlace(reg.cardinal() + i - ncpas + 1));
        }

        der.enlace(0, reg.enlace(reg.cardinal() - ncpas + 1));
        //Modificar el nodo padre
        fichero.liberarPagina(reg.dirección());
        escribir(izq);
        escribir(der);
        padre.extraer(posReg);
        padre.clave(posReg, reg.clave(reg.cardinal() - ncpas + 1));
    }

    /**
     * El procedimiento "crear" crea un árbol B y lo asocia a un fichero.
     *
     * @param ruta -> Ruta del fichero
     * @param tamañoDatos -> Tamaño en bytes de los elementos del árbol
     * @param Orden -> Órden del árbol
     */
    public void crear(String ruta, int tamañoDatos, int Orden) throws Exception {
        cerrar();
        this.tamañoDatos = tamañoDatos;
        this.Orden = Orden;
        if (Orden < 5) {
            throw new Exception("Orden inferior a 5 en arbol B");
        }
        Nodo nodo = new Nodo();
        nombreFichero = ruta;
        fichero.crear(nombreFichero, nodo.tamaño(), 4);
        raiz = fichero.dirNula;
        numElem = 0;
        minimoClaves = ((Orden + 1) / 2) - 1;
        fichero.adjunto(0, raiz);
        fichero.adjunto(1, numElem);
        fichero.adjunto(2, tamañoDatos);
        fichero.adjunto(3, Orden);
    }

    ;
                       
        /**
        *  Se crea un árbol B de órden 100 por defecto
        *  @param ruta -> Ruta del fichero
        *  @param tamañoDatos -> Tamaño en bytes de los elementos del árbol
        */
        public void crear(String ruta, int tamañoDatos) throws Exception {
        crear(ruta, tamañoDatos, 100);
    }

    ;
       
        /**
        * El procedimiento "abrir" abre el árbol B almacenado en el fichero
        * pasado por parámetro (una String con la ruta del fichero) y lo asocia al objeto.
        * @param ruta -> Ruta del fichero
        */
        public void abrir(String ruta) {
        fichero.abrir(ruta);
        raiz = fichero.adjunto(0);
        numElem = fichero.adjunto(1);
        tamañoDatos = fichero.adjunto(2);
        Orden = fichero.adjunto(3);
        minimoClaves = ((Orden + 1) / 2) - 1;
    }

    ;
       
        /**
        *El procedimiento "cerrar" cierra el fichero asociado
        *y disocia el objeto del fichero.
        */     
        public void cerrar() {
        fichero.cerrar();
    }

    ;
       
        /**
        *El procedimiento "vaciar" deja el contenedor sin ningún elemento.
        */             
        public void vaciar() throws Exception {
        fichero.cerrar();
        crear(nombreFichero, tamañoDatos);
    }

    ;
       
        /**
        * La función "insertar" añade al contenedor un nuevo elemento pasado
        * por parámetro, devuelve verdadero si lo añadió y falso en caso contrario.
        * @param e -> Elemento a insertar en el contenedor
        * @return Verdadero si lo eliminó y falso en caso contrario
        */                     
        public boolean insertar(int e) throws Exception {
        Stack pila = new Stack();
        if (buscar(e, pila) == false) {
            return false;     //No se admiten repetidos
        }
        Nodo nodoActual = new Nodo();
        InfoPila info;
        ParejaInsertar pa = new ParejaInsertar();
        pa.clave = e;
        pa.dirección = FicheroAyuda.dirNula;
        fichero.adjunto(1, ++numElem);
        if (!pila.empty()) {                    //El arbol no está vacío
            info = (InfoPila) pila.pop();
            nodoActual = info.nodo;
            int pos = info.pos;
            nodoActual.insertar(pa.clave, pa.dirección, pos + 1);
            if (nodoActual.cardinal() < Orden) {    //No hay problema
                escribir(nodoActual);
                return true;
            }

            while (!pila.empty()) {         //Arreglar sobrecarga
                info = (InfoPila) pila.pop();
                Nodo der = new Nodo(), izq = new Nodo();
                Nodo padre = info.nodo;
                pos = info.pos;
                if (pos > 0) {                   //Tiene hermano izquierdo
                    izq = leer(padre.enlace(pos - 1));
                    if (izq.cardinal() < Orden - 1) {       //Resuelto
                        rotaciónderizq(padre, pos - 1, izq, nodoActual);
                        return true;
                    }
                }

                if (pos < padre.cardinal()) {   //Tiene hermano derecho
                    der = leer(padre.enlace(pos + 1));
                    if (der.cardinal() < Orden - 1) {       //Resuelto
                        rotaciónizqder(padre, pos, nodoActual, der);
                        return true;
                    }
                }

                //No se puede rotar -> se parte
                if (pos == 0) {
                    partición_2_3(padre, pos, nodoActual, der);
                } else {
                    partición_2_3(padre, pos - 1, izq, nodoActual);
                }

                if (padre.cardinal() < Orden) {         //Resuelto
                    escribir(padre);
                    return true;
                }

                nodoActual = padre;
            }

            //Se parte la raiz
            pa = partición_1_2(nodoActual);
        }

        //Se crea una nueva raiz
        nodoActual.cardinal(1);
        nodoActual.enlace(0, raiz);
        nodoActual.clave(1, pa.clave);
        nodoActual.enlace(1, pa.dirección);
        nodoActual.dirección(fichero.tomarPagina());
        raiz = nodoActual.dirección();
        escribir(nodoActual);
        fichero.adjunto(0, raiz);
        return true;
    }

    /**
     * La función "extraer" extrae del contenedor el elemento pasado por
     * parámetro, devuelve verdadero si lo eliminó y falso en caso contrario. Si
     * no se encuentra no se altera el contenedor.
     *
     * @param e -> Elemento a extraer del contenedor
     * @return Verdadero si lo eliminó y falso en caso contrario
     */
    public boolean extraer(int e) throws Exception {
        Stack pila = new Stack();
        if (!buscar(e, pila)) {
            return false;             //Si no está, salimos sin hacer nada
        }
        fichero.adjunto(1, --numElem);
        Nodo nodoActual = new Nodo();
        InfoPila info = (InfoPila) pila.pop();
        nodoActual = info.nodo;
        int pos = info.pos;

        if (nodoActual.enlace(0) != FicheroAyuda.dirNula) {
            //Extracción desde un nodo no hoja
            pila.add(new InfoPila(info.nodo, info.pos));
            //Hay que buscar el sucesor y cambiarlo
            LinkedList cola = new LinkedList();
            int dir = nodoActual.enlace(pos);
            do {    //Descendemos por las ramas izquierdas
                nodoActual = leer(dir);
                dir = nodoActual.enlace(0);
                if (dir == fichero.dirNula) {
                    pos = 1;
                } else {
                    pos = 0;
                }
                //Guardamos el camino en una cola
                cola.addLast(new InfoPila(nodoActual, pos));
            } while (dir != FicheroAyuda.dirNula);

            info = (InfoPila) pila.pop();
            //Se sustituye por el sucesor
            info.nodo.clave(info.pos, nodoActual.clave(1));
            //Se escribe por si no hay más modificaciones
            escribir(info.nodo);
            pila.add(info);
            while (!cola.isEmpty()) {
                //Se pasa el camino de la cola a la pila
                nodoActual = ((InfoPila) cola.getFirst()).nodo;
                pila.add(cola.getFirst());
                cola.removeFirst();
            }

            info = (InfoPila) pila.pop();
            nodoActual = info.nodo;
            pos = info.pos;
        }

        //Extracción en un nodo hoja
        nodoActual.extraer(pos);
        while (nodoActual.cardinal() < minimoClaves && nodoActual.dirección() != raiz) {
            Nodo padre, der = new Nodo(), izq = new Nodo();
            info = (InfoPila) pila.pop();
            padre = info.nodo;                              //Se toma el padre de la pila
            pos = info.pos;
            if (pos < padre.cardinal()) {   //Tiene hermano derecho
                der = leer(padre.enlace(pos + 1));
                if (der.cardinal() > minimoClaves) {
                    rotaciónderizq(padre, pos, nodoActual, der);
                    return true;
                }
            }

            if (pos > 0) {                                  //Tiene hermano izquierdo
                izq = leer(padre.enlace(pos - 1));
                if (izq.cardinal() > minimoClaves) {
                    rotaciónizqder(padre, pos - 1, izq, nodoActual);
                    return true;
                }
            }

            //No se puede rotar -> re recombina
            if (pos > 0 && pos < padre.cardinal()) {
                recombinación_3_2(padre, pos, izq, nodoActual, der);
            } else if (pos > 0) {
                recombinación_2_1(padre, pos - 1, izq, nodoActual);
            } else {
                recombinación_2_1(padre, pos, nodoActual, der);
            }
            nodoActual = padre;
        }

        if (nodoActual.cardinal() > 0) {
            //Se escribe el nodo, si tiene información
            escribir(nodoActual);
        } else {
            //La raiz se ha quedado sin datos
            raiz = nodoActual.enlace(0);
            fichero.liberarPagina(nodoActual.dirección());
            fichero.adjunto(0, raiz);
        }
        return true;
    }

    /**
     * La función "cardinal" devuelve el número de elementos del contenedor.
     *
     * @return numElem = Número de elementos del contenedor
     */
    public int cardinal() {
        return numElem;
    }

}
