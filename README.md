# СПЕЦИФИКАЦИЯ

1. ## Постановка задачи
   
	Разработать программу на ЯП Java, реализующую алгоритм Прима построения МОД (для взвешенного (неотрицательные целочисленные веса) связного неориентированного графа), содержащую графический интерфейс.

3. ## Теоретическое описание алгоритма Прима.
	На вход алгоритма подаётся связный неориентированный граф. Для каждого ребра задаётся его стоимость.

	Сначала берётся произвольная вершина и находится ребро, инцидентное данной вершине и обладающее наименьшей стоимостью. Найденное ребро и соединяемые им две вершины образуют дерево. Затем, рассматриваются рёбра графа, один конец которых — уже принадлежащая дереву вершина, а другой — нет; из этих рёбер выбирается ребро наименьшей стоимости. Выбираемое на каждом шаге ребро присоединяется к дереву. Рост дерева происходит до тех пор, пока не будут исчерпаны все вершины исходного графа.

	Результатом работы алгоритма является остовное дерево минимальной стоимости.

	Псевдокод:

// Граф G = (E, V)

// Построение дерева 𝑻 = (𝑬T, 𝑽𝑻)
 
// ET – рёбра, принадлежащие T

// VT – вершины, принадлежащие T
 
// Начинаем с произвольной вершины 𝑠

𝑽𝑻 ← {𝑠} // множество вершин, принадлежащих Т

// Пока существуют вершины, не принадлежащие дереву T

while (∃v ∊ V | v ∉ VT) do

// Из ребер, соединяющих вершины 𝑻 с вершинами   графа 𝑮,

// выбираем ребро минимального веса

𝑒 ← min {𝑤𝑖,𝑗 ∣ (𝑣𝑖, 𝑣𝑗) ∈ 𝑬 ∣ 𝑣𝑖 ∈ VT, 𝑣𝑗 ∉ VT}

// Включаем его в дерево вместе с его инцидентной

// вершиной, не входящей в 𝑻

𝑬𝑻 ← 𝑬𝑻 ∪ {𝑒}
𝑽𝑻 ← 𝑽𝑻 ∪ {𝑣𝑗}
end while


## Прототип проекта

#### 1. Задание графа

Пользователю предлагается 3 способа задания графа: 

1) **Drawing input** - задание графа с помощью курсора мыши.

2) **Matrix input** - задание графа с помощью матрицы смежности.

3) **File input** - задание графа с помощью матрицы смежности, расположенной в файле.



### 1) Drawing input
![Рисунок 1 – Прототип Drawing input](https://github.com/Son-of-Henry-Ford/PrimGUI/blob/tmp/pictures/DrawingInput.png)

Пользователь задаёт граф, используя мышь (ЛКМ - добавить вершину, ПКМ - выбрать вершину инцидентную ребру). При добавлении ребра пользователю будет предложено задать его вес. Также есть возможность удалить граф или удалить отдельную вершину с инцидентными ей рёбрами с помощью кнопок Delete graph и Delete Node соответственно.


### 2) Matrix input:
![Рисунок 2 – Прототип Matrix input](https://github.com/Son-of-Henry-Ford/PrimGUI/blob/tmp/pictures/MatrixInput.png)

Пользователь задаёт размер матрицы смежности, затем заполняет её в соответствии с требованиями: должна быть симметричной, содержать неотрицательные веса рёбер.
### 3) File input:
![Рисунок 3 – Прототип Fileinput](https://github.com/Son-of-Henry-Ford/PrimGUI/blob/tmp/pictures/FileInput.png)
	Пользователь, после нажатия клавиши Fileinput задаёт в всплывающем окне расположение файла, который должен содержать матрицу смежности, отформатированную в соответствии с требованиями: веса в матрице разделены пробелами, строки матрицы разделены переносом строки.
 
#### 2. Запуск алгоритма
После задания матрицы пользователь может изменить скорость, с которой будут выполняться шаги алгоритма в секундах. 
Затем выполняется отрисовка графа с помощью кнопки Draw Graph. После построения графа пользователь запускает алгоритм Прима, при этом на каждом шаге работы алгоритма выбранные вершины и ребра окрашиваются для наглядного представления участников MST. 
Визуализация работы алгоритма сопровождается текстовой информацией о процессе построения MST. 


## 3. План работы.
01.07.24 - сдача вводного задания, согласование спецификации, демонстрация эскиза графического интерфейса.

03.07.24 - сдача прототипа (приложение, демонстрирующее интерфейс и частично реализующее основные функции).

05.07.24 - сдача 1-ой версии приложения (реализует работу алгоритма с визуализацией), исправление возможных недостатков проекта по итогам защиты.

08.07.24 - сдача 2-ой версии приложения (протестированного и отлаженного).

09.07.24 - последние штрихи в оформлении приложения, сдача отчёта по проекту.


## 4. Распределение ролей.
Семененко Анна - разработка интерфейса приложения.

Жохов Кирилл - реализация алгоритма, отчётная документация.

Рогожин Константин - разработка эскиза интерфейса, тестирование.
