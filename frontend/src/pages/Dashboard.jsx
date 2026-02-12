import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";
import { Button } from "../components/Button";
import { Input } from "../components/Input";
import toast from "react-hot-toast";
import { Plus, LayoutDashboard, LogOut, Lock, Clock, AlertCircle } from "lucide-react";
import Layout from "../components/Layout";
import { Link } from "react-router-dom";
import { clsx } from "clsx";

export default function Dashboard() {
    const { user, logout } = useAuth();
    const [tasks, setTasks] = useState([]);
    const [developers, setDevelopers] = useState([]);
    const [reviewers, setReviewers] = useState([]);
    const [loading, setLoading] = useState(true);

    // New Task Form State
    const [isCreating, setIsCreating] = useState(false);
    const [newTask, setNewTask] = useState({
        taskName: "",
        description: "",
        assigneeId: "",
        reviewerId: "",
        dueAt: ""
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [tasksRes, devsRes, reviewersRes] = await Promise.all([
                api.get("/tasks"),
                api.get("/users/role/DEVELOPER"),
                api.get("/users/role/REVIEWER"),
            ]);
            setTasks(tasksRes.data);
            setDevelopers(devsRes.data);
            setReviewers(reviewersRes.data);
        } catch (error) {
            console.error("Failed to fetch data", error);
            toast.error("Failed to load dashboard data");
        } finally {
            setLoading(false);
        }
    };

    const handleCreateTask = async (e) => {
        e.preventDefault();
        if (!newTask.assigneeId || !newTask.reviewerId) {
            toast.error("Please select an assignee and reviewer");
            return;
        }

        try {
            await api.post("/tasks", {
                ...newTask,
                assigneeId: parseInt(newTask.assigneeId),
                reviewerId: parseInt(newTask.reviewerId),
            });
            toast.success("Task created successfully");
            setIsCreating(false);
            setNewTask({ taskName: "", description: "", assigneeId: "", reviewerId: "", dueAt: "" });
            fetchData();
        } catch (error) {
            toast.error("Failed to create task");
        }
    };

    const handleStateChange = async (taskId, newState) => {
        try {
            await api.put(`/tasks/${taskId}/state?state=${newState}`);
            toast.success(`Task moved to ${newState}`);
            fetchData();
        } catch (error) {
            toast.error(`Failed to move task: ${error.response?.data?.message || error.message}`);
        }
    };

    const getColumnsForRole = () => {
        switch (user?.role) {
            case 'MANAGER': return ["BACKLOG", "IN_PROGRESS", "REVIEW", "DONE"];
            case 'DEVELOPER': return ["BACKLOG", "IN_PROGRESS", "REVIEW"];
            case 'REVIEWER': return ["REVIEW", "DONE"];
            default: return ["BACKLOG"];
        }
    };

    const getFilteredTasks = () => {
        if (!user) return [];
        return tasks.filter(task => {
            if (user.role === 'MANAGER') return true;
            if (user.role === 'DEVELOPER') return task.assigneeId === user.userId;
            if (user.role === 'REVIEWER') return task.reviewerId === user.userId;
            return false;
        });
    };

    const columns = getColumnsForRole();
    const filteredTasks = getFilteredTasks();

    // New User Form State
    const [isCreatingUser, setIsCreatingUser] = useState(false);
    const [newUser, setNewUser] = useState({
        displayName: "",
        email: "",
        password: "",
        role: "DEVELOPER"
    });

    const handleCreateUser = async (e) => {
        e.preventDefault();
        try {
            await api.post("/users", newUser);
            toast.success("User created successfully");
            setIsCreatingUser(false);
            setNewUser({ displayName: "", email: "", password: "", role: "DEVELOPER" });
            fetchData();
        } catch (error) {
            toast.error(`Failed to create user: ${error.response?.data?.message || "Error"}`);
        }
    };

    const getDeadlineStatus = (dueDate) => {
        if (!dueDate) return null;
        const now = new Date();
        const due = new Date(dueDate);
        const diff = due - now;
        const hours = diff / (1000 * 60 * 60);

        if (diff < 0) return { label: "OVERDUE", color: "bg-red-100 text-red-700" };
        if (hours < 24) return { label: "DUE SOON", color: "bg-yellow-100 text-yellow-700" };
        return { label: due.toLocaleDateString(), color: "bg-blue-50 text-blue-600" };
    };

    if (loading) return <div className="flex h-screen items-center justify-center">Loading...</div>;

    return (
        <Layout>
            <div className="mb-6 flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-900">Project Board</h2>
                <div className="flex gap-2">
                    {user?.role === 'MANAGER' && (
                        <>
                            <Button variant="outline" onClick={() => setIsCreatingUser(!isCreatingUser)}>
                                <Plus className="h-4 w-4 mr-2" />
                                New User
                            </Button>
                            <Button onClick={() => setIsCreating(!isCreating)}>
                                <Plus className="h-4 w-4 mr-2" />
                                New Task
                            </Button>
                        </>
                    )}
                </div>
            </div>

            {/* Create User/Task forms elided for brevity in this replacement but assumed present... */}
            {/* I will include the full form logic since this is a full file replace instruction */}

            {isCreatingUser && user?.role === 'MANAGER' && (
                <div className="bg-white p-6 rounded-lg shadow-md mb-8 border border-gray-100 animate-in fade-in slide-in-from-top-4">
                    <h3 className="text-lg font-medium leading-6 text-gray-900 mb-4">Create New User</h3>
                    <form onSubmit={handleCreateUser} className="space-y-4">
                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                            <Input placeholder="Display Name" required value={newUser.displayName} onChange={(e) => setNewUser({ ...newUser, displayName: e.target.value })} />
                            <Input type="email" placeholder="Email" required value={newUser.email} onChange={(e) => setNewUser({ ...newUser, email: e.target.value })} />
                            <Input type="password" placeholder="Password" required value={newUser.password} onChange={(e) => setNewUser({ ...newUser, password: e.target.value })} />
                            <select className="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm" value={newUser.role} onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}>
                                <option value="MANAGER">Manager</option>
                                <option value="DEVELOPER">Developer</option>
                                <option value="REVIEWER">Reviewer</option>
                            </select>
                        </div>
                        <div className="flex justify-end space-x-2">
                            <Button type="button" variant="ghost" onClick={() => setIsCreatingUser(false)}>Cancel</Button>
                            <Button type="submit">Create User</Button>
                        </div>
                    </form>
                </div>
            )}

            {isCreating && user?.role === 'MANAGER' && (
                <div className="bg-white p-6 rounded-lg shadow-md mb-8 border border-gray-100 animate-in fade-in slide-in-from-top-4">
                    <h3 className="text-lg font-medium leading-6 text-gray-900 mb-4">Create New Task</h3>
                    <form onSubmit={handleCreateTask} className="space-y-4">
                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                            <Input placeholder="Task Name" required value={newTask.taskName} onChange={(e) => setNewTask({ ...newTask, taskName: e.target.value })} />
                            <Input placeholder="Description" required value={newTask.description} onChange={(e) => setNewTask({ ...newTask, description: e.target.value })} />
                            <select className="flex h-10 w-full rounded-md border border-gray-300 px-3 py-2 text-sm" required value={newTask.assigneeId} onChange={(e) => setNewTask({ ...newTask, assigneeId: e.target.value })}>
                                <option value="">Select Assignee (Developer)</option>
                                {developers.map(dev => <option key={dev.userId} value={dev.userId}>{dev.userName}</option>)}
                            </select>
                            <select className="flex h-10 w-full rounded-md border border-gray-300 px-3 py-2 text-sm" required value={newTask.reviewerId} onChange={(e) => setNewTask({ ...newTask, reviewerId: e.target.value })}>
                                <option value="">Select Reviewer</option>
                                {reviewers.map(rev => <option key={rev.userId} value={rev.userId}>{rev.userName}</option>)}
                            </select>
                            <div className="sm:col-span-2">
                                <label className="text-xs text-gray-400 mb-1 block">Finish By Deadline (Optional)</label>
                                <Input type="datetime-local" value={newTask.dueAt} onChange={(e) => setNewTask({ ...newTask, dueAt: e.target.value })} />
                            </div>
                        </div>
                        <div className="flex justify-end space-x-2">
                            <Button type="button" variant="ghost" onClick={() => setIsCreating(false)}>Cancel</Button>
                            <Button type="submit">Create Task</Button>
                        </div>
                    </form>
                </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {columns.map((status) => (
                    <div key={status} className="bg-gray-100/50 p-4 rounded-xl border border-gray-200">
                        <h3 className="font-semibold text-gray-700 mb-4 flex items-center justify-between">
                            {status.replace('_', ' ')}
                            <span className="bg-gray-200 text-gray-600 py-0.5 px-2 rounded-full text-xs">
                                {filteredTasks.filter((t) => t.state === status).length}
                            </span>
                        </h3>
                        <div className="space-y-3">
                            {filteredTasks
                                .filter((task) => task.state === status)
                                .map((task) => {
                                    const deadlineStatus = getDeadlineStatus(task.dueAt);
                                    return (
                                        <div key={task.taskId} className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow relative overflow-hidden">
                                            {task.blocked && (
                                                <div className="absolute top-0 right-0 p-2 text-red-500" title="Blocked by dependencies">
                                                    <Lock className="h-4 w-4" />
                                                </div>
                                            )}

                                            <Link to={`/tasks/${task.taskId}`} className="hover:underline">
                                                <h4 className="font-medium text-gray-900 pr-6">{task.taskName}</h4>
                                            </Link>
                                            <p className="text-sm text-gray-500 mt-1 mb-3 line-clamp-2">{task.description}</p>

                                            <div className="flex flex-col space-y-2 text-xs text-gray-400 mb-4 border-b border-gray-50 pb-3">
                                                <span className="flex items-center gap-2">
                                                    <div className="w-5 h-5 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center text-[10px] font-bold">
                                                        {task.assigneeName ? task.assigneeName.substring(0, 1) : 'U'}
                                                    </div>
                                                    {task.assigneeName}
                                                </span>
                                                <span className="flex items-center gap-2">
                                                    <div className="w-5 h-5 rounded-full bg-purple-50 text-purple-600 flex items-center justify-center text-[10px] font-bold">
                                                        {task.reviewerName ? task.reviewerName.substring(0, 1) : 'U'}
                                                    </div>
                                                    {task.reviewerName} (Reviewer)
                                                </span>
                                            </div>

                                            <div className="flex items-center justify-between mb-4">
                                                {deadlineStatus && (
                                                    <span className={clsx("text-[10px] font-semibold px-2 py-0.5 rounded flex items-center gap-1", deadlineStatus.color)}>
                                                        <Clock className="h-3 w-3" />
                                                        {deadlineStatus.label}
                                                    </span>
                                                )}
                                                {task.blocked && (
                                                    <span className="text-[10px] font-semibold bg-red-50 text-red-600 px-2 py-0.5 rounded flex items-center gap-1">
                                                        <AlertCircle className="h-3 w-3" />
                                                        BLOCKED
                                                    </span>
                                                )}
                                            </div>

                                            {/* Role-Based Actions */}
                                            <div className="flex gap-2">
                                                {user.role === 'DEVELOPER' && status === "BACKLOG" && (
                                                    <Button
                                                        size="sm"
                                                        className="w-full text-xs h-8"
                                                        disabled={task.blocked}
                                                        onClick={() => handleStateChange(task.taskId, "IN_PROGRESS")}
                                                    >
                                                        {task.blocked ? "Blocked" : "Start Task"}
                                                    </Button>
                                                )}
                                                {user.role === 'DEVELOPER' && status === "IN_PROGRESS" && (
                                                    <Button
                                                        size="sm"
                                                        className="w-full text-xs h-8"
                                                        onClick={() => handleStateChange(task.taskId, "REVIEW")}
                                                    >
                                                        Submit Review
                                                    </Button>
                                                )}
                                                {user.role === 'REVIEWER' && status === "REVIEW" && (
                                                    <div className="flex w-full gap-2">
                                                        <Button
                                                            size="sm"
                                                            className="flex-1 text-xs h-8 bg-green-600 hover:bg-green-700"
                                                            onClick={() => handleStateChange(task.taskId, "DONE")}
                                                        >
                                                            Approve
                                                        </Button>
                                                        <Button
                                                            size="sm"
                                                            variant="outline"
                                                            className="flex-1 text-xs h-8 text-red-600 border-red-200"
                                                            onClick={() => handleStateChange(task.taskId, "IN_PROGRESS")}
                                                        >
                                                            Reject
                                                        </Button>
                                                    </div>
                                                )}
                                                {(user.role === 'MANAGER' || user.role === 'DEVELOPER' || user.role === 'REVIEWER') && (
                                                    <Link to={`/tasks/${task.taskId}`} className={clsx(user.role !== 'MANAGER' ? "w-1/3" : "w-full")}>
                                                        <Button size="sm" variant="ghost" className="w-full text-xs h-8">
                                                            Details
                                                        </Button>
                                                    </Link>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            {filteredTasks.filter((t) => t.state === status).length === 0 && (
                                <div className="text-center py-8 text-gray-400 text-sm border-2 border-dashed border-gray-200 rounded-lg">
                                    No tasks
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </Layout>
    );
}

