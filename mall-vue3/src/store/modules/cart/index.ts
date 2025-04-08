import { defineStore } from 'pinia';

export interface CartItem {
  id: string;
  title: string;
  cover: string;
  price: number;
  count: number;
}

interface CartState {
  items: CartItem[];
}

export const useCartStore = defineStore('cart', {
  state: (): CartState => ({
    items: [],
  }),
  getters: {
    cartCount: (state) => {
      return state.items.reduce((total, item) => total + item.count, 0);
    },
    cartTotal: (state) => {
      return state.items.reduce((total, item) => total + (item.price * item.count), 0).toFixed(2);
    },
    hasItems: (state) => state.items.length > 0,
  },
  actions: {
    addToCart(item: CartItem) {
      const existItem = this.items.find(i => i.id === item.id);
      if (existItem) {
        existItem.count += item.count || 1;
      } else {
        this.items.push({
          ...item,
          count: item.count || 1
        });
      }
    },
    removeFromCart(id: string) {
      const index = this.items.findIndex(item => item.id === id);
      if (index !== -1) {
        this.items.splice(index, 1);
      }
    },
    updateItemCount(id: string, count: number) {
      const item = this.items.find(i => i.id === id);
      if (item) {
        item.count = count;
      }
    },
    clearCart() {
      this.items = [];
    }
  },
  persist: true,
}); 