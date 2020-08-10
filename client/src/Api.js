import axios from 'axios'

const SERVER_URL = 'http://localhost:9000';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 1000
});

export default {
    createNew: (text, completed) => instance.post('wishes', {title: text, completed: completed}),
    getAll: () => instance.get('wishes', {
        transformResponse: [function(data) {
            return data? JSON.parse(data)._embedded.wishes : data;
        }]
    }),
    updateForId: (id, text, completed) => instance.put('wishes/' +id, {title: text, completed: completed}),
    removeForId: (id) => instance.delete('wishes/'+id)
}