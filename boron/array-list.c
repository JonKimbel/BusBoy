#include <stddef.h>
#include <stdlib.h>
#include "array-list.h"

bool al_init(ArrayList* arrayList, int initialLength) {
  if (arrayList == NULL) {
    return false;
  }

  arrayList->_allocatedLength = initialLength < 0 ? 0 : initialLength;
  arrayList->length = 0;
  arrayList->data = (ARRAY_LIST_TYPE *) malloc(
      arrayList->_allocatedLength *
      sizeof(ARRAY_LIST_TYPE));

  if (arrayList->_allocatedLength == 0) {
    // If we didn't try to allocate any memory, there's no way we could have
    // failed.
    return true;
  }
  return arrayList->data != NULL;
}

bool al_add(ArrayList* arrayList, ARRAY_LIST_TYPE item) {
  if (arrayList == NULL) {
    return false;
  }

  if (arrayList->length >= arrayList->_allocatedLength) {
    // There's not enough allocated space, resize the data array.
    int newAllocatedLength = arrayList->_allocatedLength <= 0
        ? 1 : arrayList->_allocatedLength * 2;
    ARRAY_LIST_TYPE* newData = (ARRAY_LIST_TYPE *) malloc(
        newAllocatedLength *
        sizeof(ARRAY_LIST_TYPE));
    if (newData == NULL) {
      // Can't allocate any more space.
      return false;
    }
    arrayList->_allocatedLength = newAllocatedLength;

    // Copy existing data to the new array.
    for (int i = 0; i < arrayList->length; i++) {
      newData[i] = arrayList->data[i];
    }

    // Free the space allocated to the old array and update the data pointer.
    free(arrayList->data);
    arrayList->data = newData;
  }

  // Update the length and set the new value.
  arrayList->length++;
  arrayList->data[arrayList->length - 1] = item;
  return true;
}

void al_clear(ArrayList* arrayList) {
  if (arrayList == NULL) {
    return;
  }

  arrayList->length = 0;
  arrayList->_allocatedLength = 0;
  free(arrayList->data);
}
