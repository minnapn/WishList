<template>
    <div>
        <h1 class="title">Önskelista</h1>       
        <section class="wishlistapp"> 
                <header class="header">
                    <input class="new-wish"
                        autofocus autocomplete="off"
                        :placeholder="this.inputPlaceholder"
                        v-model="newWish"
                        @keyup.enter="addWish">
                        <button class="add-wish-button" @click="addWish">Lägg till</button>
                </header>
                <section class="main" v-show="wishes.length" v-cloak>
                    <ul class="wish-list">
                        <li v-for="wish in filteredWishes"
                        class="wish"
                        :key="wish.id"
                        :class="{ completed: wish.completed, editing: wish == editedWish }">
                            <div class="view">
                                <input class="toggle" type="checkbox" v-model="wish.completed" @change="completeWish(wish)">
                                <label class="wish-title" 
                                @dblclick="editWish(wish)">
                                {{ wish.title }}
                                </label>
                                <button class="destroy" @click="removeWish(wish)">Ta bort</button>
                            </div>
                            <input class="edit" type="text"
                                v-show="editedWish"
                                v-model="wish.title"
                                v-wish-focus="wish == editedWish"
                                @blur="doneEdit(wish)"
                                @keyup.enter="doneEdit(wish)"
                                @keyup.esc="cancelEdit(wish)">
                        </li> 
                    </ul>
                </section>            
        </section>
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{this.error}}
        </div>
    </div>
</template>

<script>

    import api from '../Api';

// visibility filters
  let filters = {
    all: function (wishes) {
      return wishes
    },
    active: function (wishes) {
      return wishes.filter(function (wish) {
        return !wish.completed
      })
    },
    completed: function (wishes) {
      return wishes.filter(function (wish) {
        return wish.completed
      })
    }
  }

// app Vue instance
  const Wishes = {
    name: 'Wishes',
    props: {
      activeUser: Object
    },
    
    // app initial state
    data: function() {
      return {
        wishes: [],
        newWish: '',
        editedWish: null,
        visibility: 'all',
        loading: true,
        error: null,
      }
    },

    mounted() {
        api.getAll()
            .then(response => {
                this.$log.debug("Data loaded: ", response.data)
                this.wishes = response.data
            })

            .catch(error => {
                this.$log.debug(error)
                this.error = "Failed to load wishes"
            })

            .finally(() => this.loading = false)
    },

    // computed properties
    // http://vuejs.org/guide/computed.html
    computed: {
      filteredWishes: function () {
        return filters[this.visibility](this.wishes)
      },
      remaining: function () {
        return filters.active(this.wishes).length
      },
      allDone: {
        get: function () {
          return this.remaining === 0
        },
        set: function (value) {
          this.wishes.forEach(function (wish) {
            wish.completed = value
          })
        }
      },
      userEmail: function () {
        return this.activeUser ? this.activeUser.email : ''
      },
      inputPlaceholder: function () {
        return this.activeUser ? this.activeUser.given_name + ', önskning' : 'Önskning?'
      }
    },

    filters: {
      pluralize: function (n) {
        return n === 1 ? 'item' : 'items'
      }
    },

    // methods that implement data logic.
    // note there's no DOM manipulation here at all.
    methods: {

      addWish: function () {
        var value = this.newWish && this.newWish.trim()
        if (!value) {
          return
        }

        api.createNew(value, false).then( (response) => {
            this.$log.debug("New item created:", response);
            this.wishes.push({
                id: response.data.id,
                title: value,
                completed: false
            })
        }).catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to load wish"
        });

        this.newWish = ''
      },

      setVisibility: function(vis) {
        this.visibility = vis
      },

      completeWish (wish) {
            api.updateForId(wish.id, wish.title, wish.completed).then((response) => {
              this.$log.info("Item updated:", response.data);
          }).catch((error) => {
              this.$log.debug(error)
              wish.completed = !wish.completed
              this.error = "Failed to update wish"
          });
      },

      removeWish: function (wish) { // notice NOT using "=>" syntax
        api.removeForId(wish.id).then(() => {
            this.$log.debug("Item removed:", wish);
            this.wishes.splice(this.wishes.indexOf(wish), 1)
        }).catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to remove wish"
        });
      },

      editWish: function (wish) {
        this.beforeEditCache = wish.title
        this.editedWish = wish
      },

      doneEdit: function (wish) {
        if (!this.editedWish) {
          return
        }

        this.editedWish = null
        wish.title = wish.title.trim()

        if (!wish.title) {
          this.removeWish(wish)
        }
      },

      cancelEdit: function (wish) {
        this.editedWish = null
        wish.title = this.beforeEditCache
      },

      removeCompleted: function () {
        this.wishes = filters.active(this.wishes)
      },

      handleErrorClick: function () {
        this.error = null;
      },
    },

    // a custom directive to wait for the DOM to be updated
    // before focusing on the input field.
    // http://vuejs.org/guide/custom-directive.html
    directives: {
      'wish-focus': function (el, binding) {
        if (binding.value) {
          el.focus()
        }
      }
    }
  }

  export default Wishes

</script>

<style>
  [v-cloak] { display: none; }
</style>
