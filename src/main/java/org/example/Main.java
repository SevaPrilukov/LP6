package org.example;

import java.util.Arrays;

public class Main {
    static final int size = 60;
    public static void main(String[] args) {

        One();
        Two();
        Three();
    }
    public static void One() {
        float[] array = new float[size];
        Arrays.fill(array, 1.0f);
        long time = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            array[i] = (float) (array[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }

        System.out.println(array[0]);
        System.out.println(array[array.length - 1]);

        System.out.println("Время выполнения 1 метода: " + (System.currentTimeMillis() - time) + " ms");
    }
    public static void Two() {
        float[] array = new float[size];


        int numThreads = 2;
        int chunkSize = size / numThreads;

        float[] firstHalf = new float[chunkSize];
        float[] secondHalf = new float[chunkSize];

        Arrays.fill(array, 1.0f);
        long time = System.currentTimeMillis();
        System.arraycopy(array, 0, firstHalf, 0, chunkSize);
        System.arraycopy(array, chunkSize, secondHalf, 0, chunkSize);

        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < firstHalf.length; i++) {
                firstHalf[i] = (float) (firstHalf[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            System.arraycopy(firstHalf, 0, array, 0, firstHalf.length);
        });
        Thread threadTwo = new Thread(() -> {
            for (int i = 0; i < secondHalf.length; i++) {
                secondHalf[i] = (float) (secondHalf[i] * Math.sin(0.2f + (chunkSize + i) / 5) * Math.cos(0.2f + (chunkSize + i) / 5) * Math.cos(0.4f + (chunkSize + i) / 2));
            }
            System.arraycopy(secondHalf, 0, array, chunkSize, secondHalf.length);
        });
        threadOne.start();
        threadTwo.start();
        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(array[0]);
        System.out.println(array[array.length - 1]);

        System.out.println("Время выполнения 2 метода: " + (System.currentTimeMillis() - time) + " ms");
    }


    public static void Three() {
        float[] array = new float[size];
        int numThreads = 7;
        Arrays.fill(array, 1);


        long startTime = System.currentTimeMillis();
        int partSize = size / numThreads;
        int remainder = size % numThreads;


        Thread[] threads = new Thread[numThreads];
        float[][] parts = new float[partSize][numThreads];
        int sourcePosition = 0;


        for (int i = 0; i < numThreads; i++)
        {
            float[] arrayPart = new float[partSize + (i == 0 ? remainder : 0)];
            System.out.println("Поток " + (i) + " обработал " + arrayPart.length);




            System.arraycopy(array, sourcePosition, arrayPart, 0, arrayPart.length);

            int offset = sourcePosition;
            int finalI = i;
            sourcePosition += arrayPart.length;

            threads[i] = new Thread(() -> {
                for (int j = 0; j < arrayPart.length; j++)
                {
                    arrayPart[j] = (float) (arrayPart[j] * Math.sin(0.2f + (j + offset) / 5) * Math.cos(0.2f + (j + offset) / 5) * Math.cos(0.4f + (j + offset) / 2));
                }
                parts[finalI] = arrayPart;
            });

            threads[i].start();
        }

        sourcePosition = 0;

        for (int i = 0; i < numThreads; i++)
        {
            try
            {
                threads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.arraycopy(parts[i], 0, array, sourcePosition, parts[i].length);
            sourcePosition += parts[i].length;
        }



        float[] firstThreadProcessedArray = parts[0];
        System.out.println("Массив чисел, обработанных первым потоком:");
        System.out.println(Arrays.toString(firstThreadProcessedArray));


        long endTime = System.currentTimeMillis();

        System.out.println(array[0]);
        System.out.println(array[array.length - 1]);
        System.out.println("Время выполнения 3 метода: " + (endTime - startTime) + " ms");

    }
}
