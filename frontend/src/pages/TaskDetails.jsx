import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import Layout from "../components/Layout";
import { Button } from "../components/Button";
import { Input } from "../components/Input";
import toast from "react-hot-toast";
import { ArrowLeft, Clock, Save, History, Edit2, Plus, Trash2, Lock, AlertCircle } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { clsx } from "clsx";

export default function TaskDetails() {
    const { taskId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [task, setTask] = useState(null);
    const [history, setHistory] = useState([]);
    const [dependencies, setDependencies] = useState([]);
    const [deadlines, setDeadlines] = useState([]);
    const [allTasks, setAllTasks] = useState([]); // For dependency selection
    const [loading, setLoading] = useState(true);

    const [isEditing, setIsEditing] = useState(false);
    const [editedTask, setEditedTask] = useState({ taskName: "", description: "", assigneeId: "", reviewerId: "" });
    const [developers, setDevelopers] = useState([]);
    const [reviewers, setReviewers] = useState([]);

    // Extra states for adding dependency/deadline
    const [selectedBlockerId, setSelectedBlockerId] = useState("");
    const [newDeadline, setNewDeadline] = useState({ type: "FINISH_BY", dueAt: "" });

    useEffect(() => {
        fetchData();
    }, [taskId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [taskRes, historyRes, depRes, deadlineRes, devsRes, revsRes, tasksRes] = await Promise.all([
                api.get(`/tasks/${taskId}`),
                api.get(`/tasks/${taskId}/history`),
                api.get(`/tasks/${taskId}/dependencies`),
                api.get(`/tasks/${taskId}/deadlines`),
                api.get("/users/role/DEVELOPER"),
                api.get("/users/role/REVIEWER"),
                api.get("/tasks")
            ]);

            setTask(taskRes.data);
            setHistory(historyRes.data);
            setDependencies(depRes.data);
            setDeadlines(deadlineRes.data);
            setDevelopers(devsRes.data);
            setReviewers(revsRes.data);
            setAllTasks(tasksRes.data.filter(t => t.taskId !== parseInt(taskId)));

            setEditedTask({
                taskName: taskRes.data.taskName,
                description: taskRes.data.description,
                assigneeId: taskRes.data.assigneeId,
                reviewerId: taskRes.data.reviewerId,
            });
        } catch (error) {
            console.error("Failed to load data", error);
            toast.error("Failed to load task details");
        } finally {
            setLoading(false);
        }
    };

    const handleStateChange = async (newState) => {
        try {
            await api.put(`/tasks/${taskId}/state?state=${newState}`);
            toast.success(`Task moved to ${newState}`);
            fetchData();
        } catch (error) {
            toast.error(`Error: ${error.response?.data?.message || error.message}`);
        }
    };

    const handleAddDependency = async () => {
        if (!selectedBlockerId) return;
        try {
            await api.post(`/tasks/${taskId}/dependencies?blockerId=${selectedBlockerId}`);
            toast.success("Dependency added");
            setSelectedBlockerId("");
            fetchData();
        } catch (error) {
            toast.error("Failed to add dependency");
        }
    };

    const handleRemoveDependency = async (depId) => {
        try {
            await api.delete(`/tasks/dependencies/${depId}`);
            toast.success("Dependency removed");
            fetchData();
        } catch (error) {
            toast.error("Failed to remove dependency");
        }
    };

    const handleAddDeadline = async () => {
        if (!newDeadline.dueAt) return;
        try {
            await api.post(`/tasks/${taskId}/deadlines?type=${newDeadline.type}&dueAt=${newDeadline.dueAt}`);
            toast.success("Deadline added");
            setNewDeadline({ type: "FINISH_BY", dueAt: "" });
            fetchData();
        } catch (error) {
            toast.error("Failed to add deadline");
        }
    };

    const handleUpdateTask = async () => {
        try {
            await api.put(`/tasks/${taskId}`, {
                ...editedTask,
                assigneeId: parseInt(editedTask.assigneeId),
                reviewerId: parseInt(editedTask.reviewerId),
            });
            const isAuthorized = (userRole, taskId) => {
              if (userRole === 'MANAGER') return true;
              if (userRole === 'ASSIGNEE' && user.userId === task.assigneeId) return true;
              if (userRole === 'REVIEWER' && user.userId === task.reviewerId) return true;
              return false;
            };
        } catch (error) {
            toast.error("Failed to update task info");
        }
    };

    const isManager = user?.role === "MANAGER";
    const isAssignee = user?.userId === task?.assigneeId;
    const isReviewer = user?.userId === task?.reviewerId;

    if (loading) return <div className="flex h-screen items-center justify-center">Loading...</div>;
    if (!task) return null;

    return (
        <Layout>
            <Button variant="ghost" onClick={() => navigate("/")} className="mb-6 pl-0 hover:pl-2 transition-all">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Back to Board
            </Button>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div className="lg:col-span-2 space-y-6">
                    {/* Header Card */}
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                        <div className="flex justify-between items-start mb-4">
                            {!isEditing ? (
                                <div>
                                    <h1 className="text-3xl font-bold text-gray-900">{task.taskName}</h1>
                                    <div className="flex gap-2 mt-3">
                                        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-primary-50 text-primary-700 border border-primary-100">
                                            {task.state}
                                        </span>
                                        {task.blocked && (
                                            <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-100">
                                                <Lock className="h-3 w-3 mr-1" /> BLOCKED
                                            </span>
                                        )}
                                    </div>
                                </div>
                            ) : (
                                <Input
                                    value={editedTask.taskName}
                                    onChange={(e) => setEditedTask({ ...editedTask, taskName: e.target.value })}
                                    className="text-2xl font-bold"
                                />
                            )}
                            {isManager && !isEditing && (
                                <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                                    <Edit2 className="h-4 w-4 mr-2" /> Edit Info
                                </Button>
                            )}
                        </div>

                        {!isEditing ? (
                            <p className="text-gray-600 leading-relaxed whitespace-pre-wrap mt-4">{task.description}</p>
                        ) : (
                            <div className="space-y-4 mt-4">
                                <textarea
                                    className="w-full rounded-lg border border-gray-300 p-3 text-sm min-h-[120px]"
                                    value={editedTask.description}
                                    onChange={(e) => setEditedTask({ ...editedTask, description: e.target.value })}
                                />
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="space-y-1">
                                        <label className="text-[10px] uppercase font-bold text-gray-400">Assignee (Dev)</label>
                                        <select
                                            className="w-full rounded-md border border-gray-300 px-3 py-1.5 text-sm"
                                            value={editedTask.assigneeId}
                                            onChange={(e) => setEditedTask({ ...editedTask, assigneeId: e.target.value })}
                                        >
                                            <option value="">Select Assignee</option>
                                            {developers.map(d => <option key={d.userId} value={d.userId}>{d.userName}</option>)}
                                        </select>
                                    </div>
                                    <div className="space-y-1">
                                        <label className="text-[10px] uppercase font-bold text-gray-400">Reviewer</label>
                                        <select
                                            className="w-full rounded-md border border-gray-300 px-3 py-1.5 text-sm"
                                            value={editedTask.reviewerId}
                                            onChange={(e) => setEditedTask({ ...editedTask, reviewerId: e.target.value })}
                                        >
                                            <option value="">Select Reviewer</option>
                                            {reviewers.map(r => <option key={r.userId} value={r.userId}>{r.userName}</option>)}
                                        </select>
                                    </div>
                                </div>
                                <div className="flex justify-end gap-2">
                                    <Button variant="ghost" onClick={() => setIsEditing(false)}>Cancel</Button>
                                    <Button onClick={handleUpdateTask}>Save Changes</Button>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Dependencies Section */}
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                            <Lock className="h-5 w-5 text-gray-400" /> Dependencies
                        </h3>

                        <div className="space-y-3 mb-6">
                            {dependencies.length === 0 ? (
                                <p className="text-sm text-gray-400 italic">No blockers defined</p>
                            ) : (
                                dependencies.map(dep => (
                                    <div key={dep.dependencyId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-100">
                                        <div className="flex items-center gap-3">
                                            <AlertCircle className="h-4 w-4 text-red-400" />
                                            <span className="text-sm font-medium text-gray-700">Blocked by: <span className="text-gray-900">{dep.blockerTaskName}</span></span>
                                        </div>
                                        {isManager && (
                                            <Button variant="ghost" size="sm" onClick={() => handleRemoveDependency(dep.dependencyId)}>
                                                <Trash2 className="h-4 w-4 text-gray-400 hover:text-red-500" />
                                            </Button>
                                        )}
                                    </div>
                                ))
                            )}
                        </div>

                        {isManager && (
                            <div className="flex gap-2">
                                <select
                                    className="flex-1 rounded-md border border-gray-300 text-sm px-3"
                                    value={selectedBlockerId}
                                    onChange={(e) => setSelectedBlockerId(e.target.value)}
                                >
                                    <option value="">Select Blocker Task...</option>
                                    {allTasks.map(t => (
                                        <option key={t.taskId} value={t.taskId}>{t.taskName} ({t.state})</option>
                                    ))}
                                </select>
                                <Button size="sm" onClick={handleAddDependency} disabled={!selectedBlockerId}>
                                    <Plus className="h-4 w-4 mr-2" /> Add Blocker
                                </Button>
                            </div>
                        )}
                    </div>

                    {/* Actions Panel */}
                    <div className="bg-gray-50 rounded-xl border border-gray-200 p-6">
                        <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-4">Required Actions</h3>
                        <div className="flex flex-wrap gap-3">
                            {isAssignee && task.state === "BACKLOG" && (
                                <Button className="bg-primary-600 hover:bg-primary-700" disabled={task.blocked} onClick={() => handleStateChange("IN_PROGRESS")}>
                                    {task.blocked ? "Blocked" : "Start Task"}
                                </Button>
                            )}
                            {isAssignee && task.state === "IN_PROGRESS" && (
                                <Button className="bg-blue-600 hover:bg-blue-700" onClick={() => handleStateChange("REVIEW")}>Submit for Review</Button>
                            )}
                            {isReviewer && task.state === "REVIEW" && (
                                <>
                                    <Button className="bg-green-600 hover:bg-green-700" onClick={() => handleStateChange("DONE")}>Approve Task</Button>
                                    <Button variant="outline" className="text-red-600 border-red-200" onClick={() => handleStateChange("IN_PROGRESS")}>Reject / Send Back</Button>
                                </>
                            )}
                            {!isAssignee && !isReviewer && !isManager && <p className="text-sm text-gray-400 italic">No actions available for your role on this task.</p>}
                            {isManager && <p className="text-sm text-gray-500 font-medium">Managers oversee progress but do not perform state transitions.</p>}
                        </div>
                    </div>
                </div>

                {/* Sidebar: Deadlines & History */}
                <div className="space-y-6">
                    {/* Deadlines Section */}
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                            <Clock className="h-5 w-5 text-gray-400" /> Deadlines
                        </h3>
                        <div className="space-y-3 mb-6">
                            {deadlines.map(dl => (
                                <div key={dl.deadlineId} className={clsx("p-3 rounded-lg border", dl.escalated ? "bg-red-50 border-red-100" : "bg-blue-50 border-blue-100")}>
                                    <div className="flex justify-between items-center mb-1">
                                        <span className="text-[10px] font-bold uppercase tracking-wider text-gray-500">{dl.deadlineType}</span>
                                        {dl.escalated && <span className="text-[10px] font-bold text-red-600">ESCALATED</span>}
                                    </div>
                                    <p className="text-sm font-semibold text-gray-900">{new Date(dl.dueAt).toLocaleString()}</p>
                                </div>
                            ))}
                            {deadlines.length === 0 && <p className="text-sm text-gray-400 italic text-center">No deadlines set</p>}
                        </div>

                        {isManager && (
                            <div className="space-y-3 pt-4 border-t border-gray-50">
                                <select
                                    className="w-full rounded-md border border-gray-300 text-sm px-2 py-1"
                                    value={newDeadline.type}
                                    onChange={(e) => setNewDeadline({ ...newDeadline, type: e.target.value })}
                                >
                                    <option value="START_BY">Start By</option>
                                    <option value="FINISH_BY">Finish By</option>
                                </select>
                                <Input
                                    type="datetime-local"
                                    className="h-8 text-xs"
                                    value={newDeadline.dueAt}
                                    onChange={(e) => setNewDeadline({ ...newDeadline, dueAt: e.target.value })}
                                />
                                <Button size="sm" className="w-full h-8" onClick={handleAddDeadline}>Add Deadline</Button>
                            </div>
                        )}
                    </div>

                    {/* History Section */}
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                            <History className="h-5 w-5 text-gray-400" /> History
                        </h3>
                        <div className="flow-root">
                            <ul className="-mb-8">
                                {history.map((log, idx) => (
                                    <li key={log.logId} className="relative pb-8">
                                        {idx !== history.length - 1 && <span className="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-100" />}
                                        <div className="relative flex space-x-3">
                                            <div className="bg-gray-100 h-8 w-8 rounded-full flex items-center justify-center ring-4 ring-white">
                                                <Clock className="h-4 w-4 text-gray-500" />
                                            </div>
                                            <div className="min-w-0 flex-1">
                                                <p className="text-xs text-gray-500">{log.comment}</p>
                                                <p className="text-[10px] text-gray-400 mt-0.5">{new Date(log.createdAt).toLocaleString()}</p>
                                            </div>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </Layout>
    );
}
