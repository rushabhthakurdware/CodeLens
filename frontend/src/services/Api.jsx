import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
    baseURL: API_BASE_URL,
});

export const projectService = {
    // Get all projects for the dashboard
    getAllProjects: async () => {
        const response = await api.get('/projects');
        return response.data;
    },

    // Add a new project (Ingestion)
    addProject: async (projectData) => {
        const response = await api.post('/projects/add', projectData);
        return response.data;
    },

    // Get files for the logic explorer
    getProjectFiles: async (projectId) => {
        const response = await api.get(`/projects/${projectId}/files`);
        return response.data;
    },

    deleteProject: async (id) => {
        await api.delete(`/projects/${id}`);
    }
};

export default api;