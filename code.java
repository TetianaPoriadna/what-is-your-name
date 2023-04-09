import edu.duke.*;
import org.apache.commons.csv.*;
import java.io.File; 

public class BabyBirths {
   // создаем программу для вычиселния популярности имен в разные годы. 
   // после каждого метода создаем небольшой проверочный тест для того, чтобы убедиться в правильности работы кода.
   
   // для начала создаем метод для вычисления общего количества имен (женских/мужских), общего кол-ва рожденных детей,
   // а также отдельного подсчета имен по гендерному признаку:
   public void totalBirths () {
        // даем доступ к файлу
        FileResource fr = new FileResource();
        // создаем переменные, которые будем использовать далее
        int totalBirths = 0;
        int totalBoys = 0;
        int totalGirls = 0;
        int totalNames = 0;
        // создаем пустой класс для использования данных метода
        StorageResource uniqueGirlNames = new StorageResource();
        StorageResource uniqueBoyNames = new StorageResource();
        // создаем цикл для обработки файла
        for (CSVRecord rec : fr.getCSVParser(false)) {
            // извлекаем кол-во рожденных детей из ячейки с именами.
            int numBorn = Integer.parseInt(rec.get(2));
            // добавляем кол-во рожденный детей к уже существующему
            totalBirths += numBorn;
            // обрабатываем кол-во всех имен в файле
            totalNames++;
            // указываем,из какой ячейки извлекаем имена
            String name = rec.get(0);
            // создаем конструкцию "if" для сортировки имен на мужские и женские
            if (rec.get(1).equals("M")) {
                // добавляем кол-во родившихся мальчиков
                totalBoys += numBorn;
                // добавляем имя в список мужских имен
                if (!uniqueBoyNames.contains(name)) {
                    uniqueBoyNames.add(name);
                }
            }
            // если в ячеке указано другое значение (F - женский пол)
            else {
                // добавляем кол-во родившихся детей женского пола
                totalGirls += numBorn;
                // добавляем имя в список женких имен
                if (!uniqueGirlNames.contains(name)) {
                    uniqueGirlNames.add(name);
                }
            }
        }
        System.out.println("Unique boy names: " + uniqueBoyNames.size());
        System.out.println("Unique girl names: " + uniqueGirlNames.size());
        System.out.println("Total names: " + totalNames);
        System.out.println("total births = " + totalBirths);
        System.out.println("female girls = " + totalGirls);
        System.out.println("male boys = " + totalBoys);
    }
    // предоставляем доступ к парсеру для изьятия файлов:
   public CSVParser getFileParser(int year) {
        FileResource fr = new FileResource(String.format("data/us_babynames_by_year/yob%s.csv", year));
        return fr.getCSVParser(false);
    }
   // определяем имя и кол-во детей, зарегестрированных под выбранным именем:
   public int getTotalBirthsRankedHigher(int year, String name, String gender) {
        // определяем кол-во родившихся детей по заданному имени и полу
        int numOfBirths = 0;
        for (CSVRecord rec : getFileParser(year)) {
            if (rec.get(0).equals(name) && rec.get(1).equals(gender)) {
                numOfBirths = Integer.parseInt(rec.get(2));
            }
        }
        // суммируем кол-во рождений
        int totalBirths = 0;
        for (CSVRecord rec : getFileParser(year)) {
            String currentGender = rec.get(1);
            // если полученное имя не подходит И текущий гендер равен заданному И текущее кол-во рождений больше: 
            if (!rec.get(0).equals(name) && currentGender.equals(gender) && 
                Integer.parseInt(rec.get(2)) >= numOfBirths) {
                // тогда общее кол-во рождений
                totalBirths += Integer.parseInt(rec.get(2));
            }
        }
        return totalBirths;
   }
    // тестируем метод:
    public void testGetTotalBirthsRankedHigher() {
        int year = 1990;
        String name = "Drew";
        String gender = "M";
        int totalBirths = getTotalBirthsRankedHigher(year, name, gender);
        System.out.println("Total number of births of those with the same gender who " +
                            "are ranked higher than " + name + ", " + gender + " in " + year
                            + ": " + totalBirths);
    }
    // создадим метод для того, чтобы узнать, какое бы имя Вы получили, если бы были рождены в другом году:
    public void whatIsNameInYear(String name, int year, int newYear, String gender) {
        // определим степень популярномти имени в заданном году
        int rank = getRank(year, name, gender); 
        System.out.println("The rank of Owen is " + rank);
        // определяем НОВОЕ имя в другом году на этой же позиции и той же гендерной принадлежности
        String newName = getName(newYear, rank, gender);
        System.out.println(name + " born in " + year + " would be " 
                            + newName + " if born in " + newYear);
    }
    // тестируем:
    public void testWhatIsNameInYear() {
        whatIsNameInYear("Susan", 1972, 2014, "F");
    }
    // создаем метод для получения имени по "рангу" (степени популярности) в заданном году:
    public String getName(int year, int rank, String gender) {
        int currentRank = 0;
        String name = "";
        // создаем цикл для обработки каждого имени в файле
        for (CSVRecord rec : getFileParser(year)) {
            // если степень популярности равна гендеру
            if (rec.get(1).equals(gender)) {
                // возвращаем последнее имя, ранг которго соответствует полу
                if (currentRank == rank) {
                    return name;
                }
                name = rec.get(0);
                currentRank++;
            }
        }
        
        return "NO NAME";
    }
    // тестируем:
    public void testGetName() {
        int year = 1982;
        int rank = 450;
        String gender = "M";
        String name = getName(year, rank, gender);
        System.out.println("In " + year + ", the " + gender + " at rank " + rank + " was " + name);
    }
    // создаем тест для вычисления степени популярности имени в заданном году:
    public int getRank(int year, String name, String gender) {
        int rank = 1;
        for (CSVRecord rec : getFileParser(year)) {
            // если пол соответствует заданному параметру, увеличиваем ранг
            if (rec.get(1).equals(gender)) {
                // если имя совпадает с заданным параметром, возвращаем данные
                if (rec.get(0).equals(name)) {
                    return rank;
                }
                rank++;
            }
        }
        return -1;
    }
    // тестируем:
    public void testGetRank() {
        int year = 1960;
        String name = "Emily";
        String gender = "F";
        int rank = getRank(year, name, gender);
        System.out.println("Rank of " + name + ", " + gender + ", in " + year + ": " + rank);  
    }
}