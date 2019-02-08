// A dynamically sized array. Example usage:
//
// ArrayList arrayList;
//
// al_init(&arrayList, 10);
// al_add(&arrayList, &myCharacter);
// al_add(&arrayList, &myCharacter);
//
// for (int i = 0; i < arrayList.length; i++) {
//   printf("Data at %d: %s\n", i, arrayList.data[i]);
// }
//
// al_clear(&arrayList); // Can now be dropped out of scope OR added to again.

#ifndef ARRAY_LIST_INCLUDED
#define ARRAY_LIST_INCLUDED

#include <stdbool.h>
#include <stdint.h>

// Change this to change the desired datatype of the ArrayList.
#define ARRAY_LIST_TYPE uint8_t

typedef struct {
  int _allocatedLength;
  int length;
  ARRAY_LIST_TYPE* data;
} ArrayList;

// Initialize an ArrayList with the given starting length. The starting length
// is merely a guess at how large the inner array will need to be, if you don't
// care just pass 0.
// Returns false if the memory could not be allocated.
bool al_init(ArrayList* arrayList, int initialLength);

// Add an item to the end of an ArrayList, resizing if necessary.
// Returns false if memory could not be allocated for the resize.
bool al_add(ArrayList* arrayList, ARRAY_LIST_TYPE item);

// Free the space used by an ArrayList. After this is called, al_init() must be
// called before the ArrayList is used again.
void al_clear(ArrayList* arrayList);

#endif
