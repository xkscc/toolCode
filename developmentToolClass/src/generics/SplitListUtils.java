package generics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SplitListUtils {



    public static <T> List<List<T>> splitList(List<T> splitList, int splitNum){

        if(splitList == null || splitList.size() <= 0 || splitNum <= 0)
            return null;

        List<List<T>> resultList = new ArrayList<List<T>>();

        int setNumber = 0;

        T nextSwap = null;
        // 分割list集合
        Iterator<T> iterator = splitList.iterator();

        for(int i = 1; i <= Math.ceil(splitList.size() / splitNum); i++){

            List<T> tempList = new ArrayList<T>();

            if(nextSwap != null){
                tempList.add(nextSwap);
                setNumber++;
            }

            while(iterator.hasNext()){
                if(++setNumber > splitNum){
                    setNumber = 0;
                    nextSwap = iterator.next();
                    break;
                }
                tempList.add(iterator.next());
            }

            resultList.add(tempList);
        }

        return resultList;

    }
}
