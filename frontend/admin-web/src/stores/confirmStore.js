import { writable } from "svelte/store";

function createConfirmStore() {
  const { subscribe, set, update } = writable({
    show: false,
    message: "",
    resolve: null,
  });

  function open(message) {
    return new Promise((resolve) => {
      set({ show: true, message, resolve });
    });
  }

  function ok() {
    update((state) => {
      if (state.resolve) state.resolve(true);
      return { ...state, show: false, resolve: null };
    });
  }

  function cancel() {
    update((state) => {
      if (state.resolve) state.resolve(false);
      return { ...state, show: false, resolve: null };
    });
  }

  return {
    subscribe,
    open,
    ok,
    cancel,
  };
}

export const confirmStore = createConfirmStore();
