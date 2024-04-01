package com.engineerfred.kotlin.next.utils

/*Atomic Operation: FieldValue.arrayUnion() is an atomic operation provided by Firestore.
It ensures that if multiple clients are trying to update the same document's array field simultaneously,
there won't be any conflicts. Each invocation of arrayUnion() will add elements to the array without overwriting
the entire array or causing conflicts with other updates happening concurrently.

No Duplicates: When using arrayUnion(), Firestore automatically ensures that duplicate
 values are not added to the array. If you try to add a value that already exists in the array,
  Firestore will ignore it, ensuring that each value is unique within the array.

Immutable Update: Unlike ArrayList.add(), which directly modifies the ArrayList instance,
 FieldValue.arrayUnion() returns a new array with the additional elements.
 This immutability is important in Firestore because Firestore operations are asynchronous,
  and you want to ensure that each operation is consistent and doesn't mutate shared state.

Scalability: Firestore is designed to scale automatically
to handle large amounts of data and concurrent users. FieldValue.arrayUnion()
is optimized for this scalability, ensuring that array updates remain efficient even as the size of
 the array and the number of concurrent updates increase.

Overall, FieldValue.arrayUnion() provides a convenient and efficient way
 to update array fields in Firestore documents while ensuring data consistency
 and scalability in a distributed environment.*/